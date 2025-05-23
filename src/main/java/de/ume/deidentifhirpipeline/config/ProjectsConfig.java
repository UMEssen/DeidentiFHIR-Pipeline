package de.ume.deidentifhirpipeline.config;

import de.ume.deidentifhirpipeline.transfer.ImplementationsFactory;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
@Getter
@Setter
public class ProjectsConfig {
  private Map<String, ProjectConfig> projects;

  @Autowired private ImplementationsFactory implementationsFactory;

  @PostConstruct
  public void setup() throws Exception {
    for (Map.Entry<String, ProjectConfig> projectConfig : projects.entrySet()) {
      projectConfig.getValue().setup(projectConfig.getKey(), implementationsFactory);
    }
  }
}
