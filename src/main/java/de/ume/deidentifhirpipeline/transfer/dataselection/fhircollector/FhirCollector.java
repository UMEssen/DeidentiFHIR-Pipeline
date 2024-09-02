package de.ume.deidentifhirpipeline.transfer.dataselection.fhircollector;

import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.ume.deidentifhirpipeline.transfer.Utils;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.utils.FHIRPathEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class FhirCollector {

  ConcurrentHashMap<String, Resource> fhirResources = new ConcurrentHashMap<>();

  FhirCollectorConfig config;

  FHIRPathEngine fhirPathEngine;

  private IGenericClient client;

  public FhirCollector(String path) throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    config = mapper.readValue(Path.of(path).toFile(), FhirCollectorConfig.class);
    log.debug("Loaded the following FhirCollector configuration: ");
    log.debug(mapper.writeValueAsString(config));

    fhirPathEngine = new FHIRPathEngine(new HapiWorkerContext(Utils.fctx, new DefaultProfileValidationSupport(Utils.fctx)));
  }

  public FhirCollector(FhirCollectorConfig config) {
    fhirPathEngine = new FHIRPathEngine(new HapiWorkerContext(Utils.fctx, new DefaultProfileValidationSupport(Utils.fctx)));
  }

  public void fetchResources(String id) {
    config.getResources().forEach(resourceConfig -> {
      resourceConfig.keySet().parallelStream().forEach(key -> {
//      config.getResources().keySet().parallelStream().forEach(key -> {
        List<Map<String, List<String>>> map = resourceConfig.get(key);
        fetchResources(key, map, id, resourceConfig);
      });
    });
  }

  public void fetchResources(String resource, List<Map<String, List<String>>> pathDefinitions, String id, Map<String, List<Map<String, List<String>>>> resourceConfig) {
    pathDefinitions.parallelStream().forEach(pathDefinition -> {
      List<String> queries = pathDefinition.get("queries");
      List<String> fhirpaths = pathDefinition.get("fhirpaths");
      log.debug(resource);
      client = Utils.fctx.newRestfulGenericClient(config.getUrl());
      queries.parallelStream().forEach(query -> {
        String enrichedQuery = query.replace("<id>", id);
        String fullSearchQuery = String.format("%s/%s?%s", config.getUrl(), resource, enrichedQuery);
        log.debug(fullSearchQuery);

        Bundle bundle = client.search().byUrl(fullSearchQuery).returnBundle(Bundle.class).execute();
        bundle.getEntry().parallelStream().forEach(entry ->
          fhirResources.put(
              String.format("%s/%s", resource, entry.getResource().getIdPart()),
              entry.getResource()
          )
        );
        bundle.getEntry().parallelStream().forEach(entry ->
          fetchReferencedResources(client, entry.getResource(), fhirpaths, resourceConfig)
        );
      });
    });
  }

  /**
   * Execute this method at application startup otherwise we get a runtime exception
   * <code>java.lang.RuntimeException: HAPI-2200: No Cache Service Providers found. Choose between hapi-fhir-caching-caffeine (Default) and hapi-fhir-caching-guava (Android)`</code>
   */
  public void loadRuntimeCachingProvider() {
    // Just call the evaluate method to get the runtime caching service loaded.
    fhirPathEngine.evaluate(new Bundle(), "entry");
  }

  public void fetchReferencedResources(IGenericClient client, Resource resource, List<String> fhirpaths, Map<String, List<Map<String, List<String>>>> resourceConfig) {
    if( fhirpaths != null ) {
      for( String fhirpath : fhirpaths ) {
        List<Base> bases = fhirPathEngine.evaluate(resource, fhirpath);
        for (Base base : bases) {
          log.debug(base.toString());
          if( !fhirResources.containsKey(base.toString()) ) {
//            String resourceType = resource.fhirType();
//            IBaseResource readResource = client.read().resource(resourceType).withId(base.toString().split("/")[1]).execute();
//            fhirResources.put("bla", readResource);
            Bundle bundle = client.search().byUrl(base.toString().replace("/","?_id=")).returnBundle(Bundle.class).execute();
            bundle.getEntry().parallelStream().forEach(r -> {
              fhirResources.put(String.format("%s/%s", r.getResource().fhirType(), r.getResource().getIdPart()),
                  r.getResource());
            });
            for( Bundle.BundleEntryComponent entry : bundle.getEntry() ) {
              List<Map<String, List<String>>> resourceList = resourceConfig.get(entry.getResource().fhirType());
              if( resourceList != null ) {
                resourceList.forEach(map -> {
                  if (map != null) {
                    List<String> newFhirpaths = map.get("fhirpaths");
                    fetchReferencedResources(client, entry.getResource(), newFhirpaths, resourceConfig);
                  }
                });
              }
            }
          }
        }
      }
    }
  }

  public IGenericClient getHapiClient() {
    return client;
  }

  public FhirCollectorConfig getConfig() {
    return config;
  }

  public Bundle getBundle() {
    Bundle bundle = new Bundle();
    fhirResources.forEach( (id, resource) -> bundle.addEntry().setResource(resource) );
    return bundle;
  }

  public String toString() {
    return fhirResources.entrySet().toString();
  }

}
