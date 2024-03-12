package de.ume.deidentifhirpipeline.configuration.datastoring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("data-storing")
public class FiremetricsDataStoringConfiguration {
  @Getter
  @Setter
  private String host;

  @Getter
  @Setter
  private int port;

  @Getter
  @Setter
  private String database;

  @Getter
  @Setter
  private String user;

  @Getter
  @Setter
  private String password;

  @Getter
  @Setter
  private boolean writeBundlesToFiles;
}
