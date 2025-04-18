package de.ume.deidentifhirpipeline.plugin;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.dataselection.DataSelection;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;

@Slf4j
@Component("data-selection.plugin-implementation-a") // Pattern must be "cohort-selection.<plugin-name>" where <plugin-name> is configured in application.yaml
public class PluginDataSelectionImplementation implements DataSelection {


  @Override
  public void before(ProjectConfig projectConfig) throws Exception {
    // Nothing
  }

  @Override
  public Context process(Context context) throws Exception {
    log.info("data-selection plugin implementation called");
    log.info("Config as map: " + context.getProjectConfig().getDataSelection().getViaPlugin().getConfig().toString());

    log.info("ID:" + context.getPatientId());

    Bundle bundle = new Bundle();
    bundle.setId("1234");

    context.setBundle(bundle);
    return context;
  }
}
