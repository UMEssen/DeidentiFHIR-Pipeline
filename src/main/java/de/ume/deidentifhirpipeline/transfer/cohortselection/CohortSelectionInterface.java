package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.cohortselection.CohortSelectionInterfaceConfig;

import java.util.List;

public interface CohortSelectionInterface {

  List<String> before(ProjectConfig projectConfig) throws Exception;
}
