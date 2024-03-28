package de.ume.deidentifhirpipeline.configuration.datastoring;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiremetricsDataStoringConfiguration {
  private String host;
  private int port;
  private String database;
  private String user;
  private String password;
  private boolean writeBundlesToFiles;
}
