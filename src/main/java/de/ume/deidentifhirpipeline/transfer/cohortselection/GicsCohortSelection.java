package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.service.GicsService;

import java.util.List;

public class GicsCohortSelection extends CohortSelection {

  @Override
  public List<String> before(ProjectConfig projectConfig) throws Exception {
    GicsService gicsService = new GicsService(projectConfig.getCohortSelection().getGics());
    List<String> ids = gicsService.getIdsWithAcceptedPolicies(
        projectConfig.getCohortSelection().getGics().getDomain(),
        projectConfig.getCohortSelection().getGics().getPolicies());
    return ids;
  }

}
