package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;

import java.util.List;

public abstract class CohortSelection {
  public abstract List<String> before(ProjectConfiguration projectConfiguration) throws Exception;

  public static List<String> beforeExecution(ProjectConfiguration projectConfiguration) throws Exception {
    return projectConfiguration.getCohortSelectionImpl().before(projectConfiguration);
  }
}
