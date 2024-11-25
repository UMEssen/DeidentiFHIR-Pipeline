package de.ume.deidentifhirpipeline.transfer.datastoring;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.datastoring.FhirServerDataStoringConfiguration;
import de.ume.deidentifhirpipeline.transfer.Utils;
import de.ume.deidentifhirpipeline.transfer.Context;
import org.hl7.fhir.r4.model.Bundle;

public class FhirServerDataStoring extends DataStoring {

  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    // Nothing to do before processing
  }

  public Context process(Context context) {
    FhirServerDataStoringConfiguration configuration = context.getProjectConfiguration().getDataStoring().getFhirServer();
    try {
      storeBundle(configuration, context.getBundle());
      return context;
    } catch (Exception e) {
      return Utils.handleException(context, e);
    }
  }

  private static void storeBundle(FhirServerDataStoringConfiguration configuration, Bundle bundle) {
    IGenericClient client = Utils.hapiClient(configuration.getUrl());
    if (configuration.getBasicAuth() != null) {
      client = Utils.hapiClient(configuration.getUrl(), configuration.getBasicAuth());
    } else if (configuration.getTokenAuth() != null) {
      client = Utils.hapiClient(configuration.getUrl(), configuration.getTokenAuth());
    }
    bundle.setType(Bundle.BundleType.TRANSACTION);
    bundle.getEntry().stream()
        .forEach(entry -> entry.getRequest()
            .setUrl(entry.getResource().fhirType() + "/" + entry.getResource().getIdElement().getIdPart())
            .setMethod(Bundle.HTTPVerb.PUT));
    client.transaction().withBundle(bundle).execute();
  }
}
