package de.ume.deidentifhirpipeline.transfer.dataselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.dataselection.FhirCollectorDataSelectionConfig;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import de.ume.deidentifhirpipeline.transfer.dataselection.fhircollector.FhirCollector;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;

@Slf4j
@Component("data-selection.fhir-collector")
public class FhirCollectorDataSelection implements DataSelection {

  @Override
  public void before(ProjectConfig projectConfig) throws Exception {
    // Nothing to do before processing
  }

  @Override
  public Bundle process(Context context) throws Exception {
    FhirCollectorDataSelectionConfig config = context.getProjectConfig().getDataSelection().getFhirCollector();
    FhirCollector fhirCollector = new FhirCollector(config.getConfigurationFile());
    fhirCollector.fetchResources(context.getPatientId());
    log.debug("Collected resources: {}", fhirCollector.toString());
    log.debug(Utils.fhirBundleToString(fhirCollector.getBundle()));
    Bundle bundle = fhirCollector.getBundle();
    if (bundle == null || bundle.getEntry().isEmpty())
      throw new Exception("Returned bundle is empty. No medical data for id: " + context.getPatientId());

    return bundle;
  }

}
