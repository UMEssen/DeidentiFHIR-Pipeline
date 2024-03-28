package de.ume.deidentifhirpipeline.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
@Getter
@Setter
public class ProjectsConfiguration {
  private Map<String,ProjectConfiguration> projects;
}
