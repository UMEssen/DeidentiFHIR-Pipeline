package de.ume.deidentifhirpipeline.transfer.dataselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;
import org.hl7.fhir.r4.model.Bundle;

public interface DataSelection {

  void before(ProjectConfig projectConfig) throws Exception;

  Bundle process(Context context) throws Exception;

}
