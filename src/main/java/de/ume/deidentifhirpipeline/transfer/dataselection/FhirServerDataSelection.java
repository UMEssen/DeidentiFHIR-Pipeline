package de.ume.deidentifhirpipeline.transfer.dataselection;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.dataselection.FhirServerDataSelectionConfiguration;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;

@Slf4j
public class FhirServerDataSelection extends DataSelection {

  private final FhirServerDataSelectionConfiguration dataSelectionConfiguration;

  public FhirServerDataSelection(FhirServerDataSelectionConfiguration dataSelectionConfiguration) {
    this.dataSelectionConfiguration = dataSelectionConfiguration;
  }

  @Override
  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    // Nothing to do before processing
  }

  @Override
  public Context process(Context context) {
    try {
      String fhirId = getFhirId(context.getPatientId(), context.getProjectConfiguration().getDataSelection().getFhirServer());
      Bundle bundle = getBundle(fhirId, context.getProjectConfiguration().getDataSelection().getFhirServer());
      context.setBundle(bundle);


//      FhirPathR4 fhirPathR4 = new FhirPathR4(Utils.fctx);
//      List<IBase> list = fhirPathR4.evaluate(bundle, "Bundle.entry.resource.ofType(Patient)", IBase.class);
//      for( IBase element : list ) {
//        switch (element) {
//          case DateType d -> System.out.println("Date: " + d.getValue());
//          case BooleanType b -> System.out.println("BooleanType: " + b.getValue());
//          case Patient p -> System.out.println("Patient: " + p.getIdPart() + p.fhirType());
//          default -> System.out.println("Not found");
//        }
//      }


      return context;
    } catch (Exception e) {
      return Utils.handleException(context, e);
    }
  }

  private static String getFhirId(String id, FhirServerDataSelectionConfiguration dataSelectionConfiguration) {
    IGenericClient client = Utils.fctx.newRestfulGenericClient(dataSelectionConfiguration.getUrl());

    // Replace configured placeholder with actual id and then execute query
    String query = dataSelectionConfiguration.getFhirIdQuery();
    String queryPlaceholder = dataSelectionConfiguration.getFhirIdQueryPlaceholder();
    String queryWithId = query.replace(queryPlaceholder, id);
    Bundle bundle = client.search().byUrl(String.format("%s/%s", dataSelectionConfiguration.getUrl(), queryWithId)).returnBundle(Bundle.class).execute();
    log.debug("getFhirId(): " + Utils.fhirBundleToString(bundle));
    if( bundle.getTotal() >= 2 ) log.warn("Data selection query returned more than one Patient resource");
    String fhirId = bundle.getEntryFirstRep().getResource().getIdPart();
    return fhirId;
  }

  private static Bundle getBundle(String fhirId, FhirServerDataSelectionConfiguration dataSelectionConfiguration) throws Exception {
    IGenericClient client = Utils.fctx.newRestfulGenericClient(dataSelectionConfiguration.getUrl());
    if( dataSelectionConfiguration.getBasic() != null ){
      client.registerInterceptor(new BasicAuthInterceptor(dataSelectionConfiguration.getBasic().getUser(),dataSelectionConfiguration.getBasic().getPassword()));
    }
    String query = dataSelectionConfiguration.getBundleQuery();
    String queryPlaceholder = dataSelectionConfiguration.getBundleQueryPlaceholder();
    String queryWithId = query.replace(queryPlaceholder, fhirId);
    Bundle bundle = client.search().byUrl(String.format( "%s/%s", dataSelectionConfiguration.getUrl(), queryWithId)).returnBundle(Bundle.class).execute();
    if( bundle == null || bundle.getTotal() == 0 ) throw new Exception("Returned bundle is empty. No medical data for id: " + fhirId);
    log.debug("getBundle(): " + Utils.fhirBundleToString(bundle));
    return bundle;
  }

}
