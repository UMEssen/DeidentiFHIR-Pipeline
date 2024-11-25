package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;

import java.util.List;

public class IdCohortSelection extends CohortSelection {
  @Override
  public List<String> before(ProjectConfig projectConfig) throws Exception {
    // return cohortSelectionConfiguration.getIds();
    return projectConfig.getCohortSelection().getViaIds().getIds();
  }

}
