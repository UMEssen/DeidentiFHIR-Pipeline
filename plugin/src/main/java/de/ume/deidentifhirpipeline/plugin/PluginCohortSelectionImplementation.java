package de.ume.deidentifhirpipeline.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Cohort;
import de.ume.deidentifhirpipeline.transfer.cohortselection.CohortSelection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component("cohort-selection.plugin-impl") // Pattern must be "cohort-selection.<plugin-name>" where <plugin-name> is configured in application.yaml
public class PluginCohortSelectionImplementation implements CohortSelection {

  @Override
  public Cohort before(ProjectConfig projectConfig) throws Exception {
    log.info("cohort-selection plugin implementation called");
    log.info("Config as map: " + projectConfig.getCohortSelection().getViaPlugin().getConfig().toString());

    ObjectMapper objectMapper = new ObjectMapper();
    PluginConfig config = objectMapper.convertValue(projectConfig.getCohortSelection().getViaPlugin().getConfig(), PluginConfig.class);
    log.info("Config as class: " + config.getUrl());

    return new Cohort(
        List.of("1"),                           // List of cohort ids that should be transfered
        Map.of("2", "some error message")   // optional: Map of filtered out ids with error messages (for logging purposes)
    );
  }
}
