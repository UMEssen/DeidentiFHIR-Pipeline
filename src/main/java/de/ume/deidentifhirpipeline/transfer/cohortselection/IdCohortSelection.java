package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Cohort;
import org.springframework.stereotype.Component;

@Component("cohort-selection.via-ids")
public class IdCohortSelection implements CohortSelection {
  @Override
  public Cohort before(ProjectConfig projectConfig) throws Exception {
    // return cohortSelectionConfiguration.getIds();
    return new Cohort(projectConfig.getCohortSelection().getViaIds().getIds(), null);
  }

}
