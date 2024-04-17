package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.cohortselection.IdCohortSelectionConfiguration;

import java.util.List;

public class IdCohortSelection extends CohortSelection {
  @Override
  public List<String> before(ProjectConfiguration projectConfiguration) throws Exception {
//    return cohortSelectionConfiguration.getIds();
    return projectConfiguration.getCohortSelection().getViaIds().getIds();
  }

}
