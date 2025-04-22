package de.ume.deidentifhirpipeline.transfer.datastoring;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.datastoring.FhirServerDataStoringConfig;
import de.ume.deidentifhirpipeline.transfer.Utils;
import de.ume.deidentifhirpipeline.transfer.Context;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;

@Component("data-storing.fhir-server")
public class FhirServerDataStoring implements DataStoring {

  public void before(ProjectConfig projectConfig) throws Exception {
    // Nothing to do before processing
  }

  public void process(Context context) throws Exception {
    FhirServerDataStoringConfig config = context.getProjectConfig().getDataStoring().getFhirServer();
    storeBundle(config, context.getBundle());
  }

  private static void storeBundle(FhirServerDataStoringConfig config, Bundle bundle) {
    IGenericClient client = Utils.hapiClient(config.getUrl());
    if (config.getBasicAuth() != null) {
      client = Utils.hapiClient(config.getUrl(), config.getBasicAuth());
    } else if (config.getTokenAuth() != null) {
      client = Utils.hapiClient(config.getUrl(), config.getTokenAuth());
    }
    bundle.setType(Bundle.BundleType.TRANSACTION);
    bundle.getEntry().stream()
        .forEach(entry -> entry.getRequest()
            .setUrl(entry.getResource().fhirType() + "/" + entry.getResource().getIdElement().getIdPart())
            .setMethod(Bundle.HTTPVerb.PUT));
    client.transaction().withBundle(bundle).execute();
  }
}
