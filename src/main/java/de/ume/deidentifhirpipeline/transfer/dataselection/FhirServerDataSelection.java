package de.ume.deidentifhirpipeline.transfer.dataselection;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.dataselection.FhirServerDataSelectionConfiguration;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.OptionalLong;

@Slf4j
public class FhirServerDataSelection extends DataSelection {

  @Override
  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    // Nothing to do before processing
  }

  @Override
  public Context process(Context context) {
    try {
      FhirServerDataSelectionConfiguration configuration = context.getProjectConfiguration().getDataSelection().getFhirServer();

      // Setup fhir client
      IGenericClient client = Utils.hapiClient(configuration.getUrl());
      if (configuration.getBasicAuth() != null) {
        client = Utils.hapiClient(configuration.getUrl(), configuration.getBasicAuth());
      } else if (configuration.getTokenAuth() != null) {
        client = Utils.hapiClient(configuration.getUrl(), configuration.getTokenAuth());
      }

      String fhirId = getFhirId(client, context.getPatientId(), configuration);
      context.setNewLastUpdated(OptionalLong.of(ZonedDateTime.now().toInstant().toEpochMilli()));
      Bundle bundle = getBundle(client, fhirId, configuration, context.getOldLastUpdated());
      context.setBundle(bundle);

      // FhirPathR4 fhirPathR4 = new FhirPathR4(Utils.fctx);
      // List<IBase> list = fhirPathR4.evaluate(bundle, "Bundle.entry.resource.ofType(Patient)",
      // IBase.class);
      // for( IBase element : list ) {
      // switch (element) {
      // case DateType d -> System.out.println("Date: " + d.getValue());
      // case BooleanType b -> System.out.println("BooleanType: " + b.getValue());
      // case Patient p -> System.out.println("Patient: " + p.getIdPart() + p.fhirType());
      // default -> System.out.println("Not found");
      // }
      // }

      return context;
    } catch (Exception e) {
      return Utils.handleException(context, e);
    }
  }

  private static String getFhirId(IGenericClient client, String id, FhirServerDataSelectionConfiguration dataSelectionConfiguration) {

    // Replace configured placeholder with actual id and then execute query
    String query = dataSelectionConfiguration.getFhirIdQuery();
    String queryPlaceholder = dataSelectionConfiguration.getFhirIdQueryPlaceholder();
    String queryWithId = query.replace(queryPlaceholder, id);
    Bundle bundle = client.search().byUrl(String.format("%s/%s", dataSelectionConfiguration.getUrl(), queryWithId)).returnBundle(Bundle.class).execute();
    log.debug("getFhirId(): " + Utils.fhirBundleToString(bundle));
    if (bundle.getTotal() >= 2)
      log.warn("Data selection query returned more than one Patient resource");
    String fhirId = bundle.getEntryFirstRep().getResource().getIdPart();
    return fhirId;
  }

  private static Bundle getBundle(IGenericClient client, String fhirId, FhirServerDataSelectionConfiguration dataSelectionConfiguration, OptionalLong lastUpdated)
      throws Exception {
    String query = dataSelectionConfiguration.getBundleQuery();
    String queryPlaceholder = dataSelectionConfiguration.getBundleQueryPlaceholder();
    String queryWithId = query.replace(queryPlaceholder, fhirId);
    String bundleQueryLastUpdatedPlaceholder = dataSelectionConfiguration.getBundleQueryLastUpdatedPlaceholder();
    if (lastUpdated.isPresent()) {
      queryWithId =
          queryWithId.replace(bundleQueryLastUpdatedPlaceholder, Utils.zonedDateTimeToFhirSearchString(Utils.longToZonedDateTime(lastUpdated.getAsLong(), ZoneId.of("UTC"))));
    }
    Bundle bundle = client.search().byUrl(String.format("%s/%s", dataSelectionConfiguration.getUrl(), queryWithId)).returnBundle(Bundle.class).execute();
    log.debug("getBundle(): " + Utils.fhirBundleToString(bundle));
    if (bundle == null || bundle.getEntry().isEmpty())
      throw new Exception("Returned bundle is empty. No medical data for id: " + fhirId);
    return bundle;
  }

}
