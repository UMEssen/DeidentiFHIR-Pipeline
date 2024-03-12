package de.ume.deidentifhirpipeline.configuration.cohortselection;

import de.ume.deidentifhirpipeline.configuration.service.GicsServiceConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("cohort-selection")
public class CohortSelectionConfiguration {
  @Getter
  @Setter
  private GicsServiceConfiguration gics;

  @Getter
  @Setter
  private FiremetricsCohortSelectionConfiguration firemetrics;

  @Getter
  @Setter
  private IdCohortSelectionConfiguration viaIds;
}
