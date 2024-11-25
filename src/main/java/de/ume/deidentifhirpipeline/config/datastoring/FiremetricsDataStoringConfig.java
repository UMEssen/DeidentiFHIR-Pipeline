package de.ume.deidentifhirpipeline.config.datastoring;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiremetricsDataStoringConfig {
  private String host;
  private int port;
  private String database;
  private String user;
  private String password;
  private boolean writeBundlesToFiles;
}
