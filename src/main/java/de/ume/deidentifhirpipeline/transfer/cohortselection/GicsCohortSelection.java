package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.service.GicsServiceConfiguration;
import de.ume.deidentifhirpipeline.service.GicsService;

import java.util.List;

public class GicsCohortSelection extends CohortSelection {

  @Override
  public List<String> before(ProjectConfiguration projectConfiguration) throws Exception {
    GicsService gicsService = new GicsService(projectConfiguration.getCohortSelection().getGics());
    List<String> ids = gicsService.getIdsWithAcceptedPolicies(
        projectConfiguration.getCohortSelection().getGics().getDomain(),
        projectConfiguration.getCohortSelection().getGics().getPolicies()
    );
    return ids;
  }

}
