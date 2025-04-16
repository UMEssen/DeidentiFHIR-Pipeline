package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;

import java.util.List;

public interface CohortSelection {

  List<String> before(ProjectConfig projectConfig) throws Exception;
}
