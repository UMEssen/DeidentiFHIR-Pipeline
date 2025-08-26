package de.ume.deidentifhirpipeline.plugin;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.datastoring.DataStoring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("data-storing.plugin-implementation-b") // Pattern must be "cohort-selection.<plugin-name>" where <plugin-name> is configured in application.yaml
public class PluginDataStoringImplementation implements DataStoring {

  @Autowired
  RandomService randomService;

  @Override
  public void before(ProjectConfig projectConfig) throws Exception {
    // Nothing to do
  }

  @Override
  public void process(Context context) throws Exception {
    log.info("data-storing plugin implementation called");
    log.info("Config as map: " + context.getProjectConfig().getDataSelection().getViaPlugin().getConfig().toString());

    log.info("ID:" + context.getPatientId());
    randomService.bla();

    log.info("Bundle to store: " + context.getBundle().getIdPart());
  }


}
