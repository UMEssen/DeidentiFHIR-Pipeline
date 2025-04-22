package de.ume.deidentifhirpipeline.transfer.dataselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;

public interface DataSelection {
  void before(ProjectConfig projectConfig) throws Exception;

  void process(Context context) throws Exception;

}
