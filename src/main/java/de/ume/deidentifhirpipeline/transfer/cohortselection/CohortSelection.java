package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Cohort;

public interface CohortSelection {

  Cohort before(ProjectConfig projectConfig) throws Exception;
}
