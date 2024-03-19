package de.ume.deidentifhirpipeline.configuration.datastoring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("data-storing")
public class DataStoringConfiguration {
  @Getter
  @Setter
  private FhirServerDataStoringConfiguration fhirServer;
  @Getter
  @Setter
  private FiremetricsDataStoringConfiguration firemetrics;
}
