package de.ume.deidentifhirpipeline.transfer.datastoring;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;

public interface DataStoring {
  void before(ProjectConfig projectConfig) throws Exception;

  void process(Context context) throws Exception;

}
