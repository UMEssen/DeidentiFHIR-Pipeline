package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;

import java.util.List;

public abstract class CohortSelection {
  public abstract List<String> before(ProjectConfig projectConfig) throws Exception;

  public static List<String> beforeExecution(ProjectConfig projectConfig) throws Exception {
    return projectConfig.getCohortSelectionImpl().before(projectConfig);
  }
}
