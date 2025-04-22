package de.ume.deidentifhirpipeline.transfer.pseudonymization;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;

public interface Pseudonymization {
  void before(ProjectConfig projectConfig) throws Exception;

  void process(Context context) throws Exception;

}
