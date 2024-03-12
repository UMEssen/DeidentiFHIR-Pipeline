package de.ume.deidentifhirpipeline.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
public class ProjectsConfiguration {
  @Getter
  @Setter
  private Map<String,ProjectConfiguration> projects;

  @Getter
  @Setter
  private TextPseudonymizationConfiguration textPseudonymization;
}
