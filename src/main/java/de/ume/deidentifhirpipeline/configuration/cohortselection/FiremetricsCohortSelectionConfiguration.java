package de.ume.deidentifhirpipeline.configuration.cohortselection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiremetricsCohortSelectionConfiguration {
  private String host;
  private int port;
  private String database;
  private String user;
  private String password;
  private String query;
  private String queryFile;
}
