package de.ume.deidentifhirpipeline.transfer.datastoring;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.datastoring.FhirServerDataStoringConfiguration;
import de.ume.deidentifhirpipeline.transfer.Utils;
import de.ume.deidentifhirpipeline.transfer.Context;
import org.hl7.fhir.r4.model.Bundle;

public class FhirServerDataStoring extends DataStoring {

  FhirServerDataStoringConfiguration configuration;

  public FhirServerDataStoring(FhirServerDataStoringConfiguration configuration) {
    this.configuration = configuration;
  }

  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    // Nothing to do before processing
  }

  public Context process(Context context) {
    try {
      storeBundle(context.getBundle());
      return context;
    } catch (Exception e) {
      return Utils.handleException(context, e);
    }
  }

  private void storeBundle(Bundle bundle) {
    IGenericClient client = Utils.fctx.newRestfulGenericClient(configuration.getUrl());
    bundle.setType(Bundle.BundleType.TRANSACTION);
    bundle.getEntry().stream()
        .forEach(entry -> entry.getRequest()
            .setUrl(entry.getResource().fhirType() + "/" + entry.getResource().getIdElement().getIdPart())
            .setMethod(Bundle.HTTPVerb.PUT));
    client.transaction().withBundle(bundle).execute();
  }
}
