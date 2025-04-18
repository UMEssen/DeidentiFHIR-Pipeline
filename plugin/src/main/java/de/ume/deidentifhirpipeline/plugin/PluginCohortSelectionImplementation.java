package de.ume.deidentifhirpipeline.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.cohortselection.CohortSelection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("cohort-selection.plugin-impl") // Pattern must be "cohort-selection.<plugin-name>" where <plugin-name> is configured in application.yaml
public class PluginCohortSelectionImplementation implements CohortSelection {

  @Override
  public List<String> before(ProjectConfig projectConfig) throws Exception {
    System.out.println("cohort-selection plugin implementation called");
    System.out.println("Config as map: " + projectConfig.getCohortSelection().getViaPlugin().getConfig().toString());

    ObjectMapper objectMapper = new ObjectMapper();
    PluginConfig config = objectMapper.convertValue(projectConfig.getCohortSelection().getViaPlugin().getConfig(), PluginConfig.class);
    System.out.println("Config as class: " + config.getUrl());

    return List.of();
  }
}
