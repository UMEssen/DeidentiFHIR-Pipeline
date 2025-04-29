package de.ume.deidentifhirpipeline.transfer.pseudonymization;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;
import org.hl7.fhir.r4.model.Bundle;

public interface Pseudonymization {
  void before(ProjectConfig projectConfig) throws Exception;

  Bundle process(Context context) throws Exception;

}
