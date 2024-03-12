package de.ume.deidentifhirpipeline.configuration.datastoring;

import de.ume.deidentifhirpipeline.configuration.auth.BasicAuthConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("data-storing")
public class FhirServerDataStoringConfiguration {
  @Getter
  @Setter
  private String url;
  @Getter
  @Setter
  private BasicAuthConfiguration basic;

  public String toString() {
    return String.format("DataStoringConfiguration(url=%s)",url);
  }
}
