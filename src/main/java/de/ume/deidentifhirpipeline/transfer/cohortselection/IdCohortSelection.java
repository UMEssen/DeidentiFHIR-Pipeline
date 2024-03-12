package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.cohortselection.IdCohortSelectionConfiguration;

import java.util.List;

public class IdCohortSelection extends CohortSelection {

  private final IdCohortSelectionConfiguration cohortSelectionConfiguration;

  public IdCohortSelection(IdCohortSelectionConfiguration cohortSelectionConfiguration) {
    this.cohortSelectionConfiguration = cohortSelectionConfiguration;
  }

  @Override
  public List<String> before(ProjectConfiguration projectConfiguration) throws Exception {
    return cohortSelectionConfiguration.getIds();
  }

}
