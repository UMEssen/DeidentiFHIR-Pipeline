package de.ume.deidentifhirpipeline.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
@Getter
@Setter
public class ProjectsConfig {
  private Map<String, ProjectConfig> projects;
}
