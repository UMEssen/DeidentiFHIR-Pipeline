package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("cohort-selection.via-ids")
public class IdCohortSelection implements CohortSelection {
  @Override
  public List<String> before(ProjectConfig projectConfig) throws Exception {
    // return cohortSelectionConfiguration.getIds();
    return projectConfig.getCohortSelection().getViaIds().getIds();
  }

}
