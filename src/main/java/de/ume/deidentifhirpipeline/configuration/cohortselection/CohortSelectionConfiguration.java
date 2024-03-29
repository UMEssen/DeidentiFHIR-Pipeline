package de.ume.deidentifhirpipeline.configuration.cohortselection;

import de.ume.deidentifhirpipeline.configuration.service.GicsServiceConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CohortSelectionConfiguration {
  private GicsServiceConfiguration gics;
  private FiremetricsCohortSelectionConfiguration firemetrics;
  private IdCohortSelectionConfiguration viaIds;
}
