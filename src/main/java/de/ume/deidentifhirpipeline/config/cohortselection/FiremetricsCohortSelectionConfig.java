package de.ume.deidentifhirpipeline.config.cohortselection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiremetricsCohortSelectionConfig {
  private String host;
  private int    port;
  private String database;
  private String user;
  private String password;
  private String query;
  private String queryFile;
}
