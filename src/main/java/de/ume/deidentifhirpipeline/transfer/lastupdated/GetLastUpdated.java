package de.ume.deidentifhirpipeline.transfer.lastupdated;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;

public interface GetLastUpdated {
  void before(ProjectConfig projectConfig) throws Exception;

  Context process(Context context) throws Exception;

  default void beforeExecution(ProjectConfig projectConfig) throws Exception {
    if (projectConfig.getLastUpdated() != null)
      this.before(projectConfig);
  }

}
