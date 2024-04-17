package de.ume.deidentifhirpipeline.transfer.dataselection;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.dataselection.FhirServerDataSelectionConfiguration;
import de.ume.deidentifhirpipeline.configuration.dataselection.DummyDataSelectionConfiguration;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import org.hl7.fhir.r4.model.Bundle;

public class DummyDataSelection extends DataSelection {

  @Override
  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    // Nothing to do before processing
  }

  @Override
  public Context process(Context context) {
    System.out.println("HapiBla data selection.");
    try {
//      String fhirId = getFhirId(context.getPatientId(), context.getProjectConfiguration().getDataSelection().getFhirServer());
      Bundle bundle = new Bundle();
      context.setBundle(bundle);
      return context;
    } catch (Exception e) {
      return Utils.handleException(context, e);
    }
  }

  private static String getFhirId(String id, FhirServerDataSelectionConfiguration dataSelectionConfiguration) {
    IGenericClient client = Utils.fctx.newRestfulGenericClient(dataSelectionConfiguration.getUrl());
    Bundle bundle = client.search().byUrl("Patient?identifier="+id).returnBundle(Bundle.class).execute();
    //    System.out.println("getFhirId(): " + Utils.fhirBundleToString(bundle));
    String fhirId = bundle.getEntryFirstRep().getResource().getIdPart();
    return fhirId;
  }

  private static Bundle getBundle(String fhirId, FhirServerDataSelectionConfiguration dataSelectionConfiguration) throws Exception {
    IGenericClient client = Utils.fctx.newRestfulGenericClient(dataSelectionConfiguration.getUrl());
    if( dataSelectionConfiguration.getBasic() != null ){
      client.registerInterceptor(new BasicAuthInterceptor(dataSelectionConfiguration.getBasic().getUser(),dataSelectionConfiguration.getBasic().getPassword()));
    }
    Bundle bundle = client.search().byUrl(String.format(dataSelectionConfiguration.getUrl() + "/Patient/%s/$everything?_count=100000", fhirId)).returnBundle(Bundle.class).execute();
    if( bundle == null ) throw new Exception("Returned bundle is empty. No medical data for id: " + fhirId);
    //    System.out.println("getBundle(): " + Utils.fhirBundleToString(bundle));
    return bundle;
  }

}
