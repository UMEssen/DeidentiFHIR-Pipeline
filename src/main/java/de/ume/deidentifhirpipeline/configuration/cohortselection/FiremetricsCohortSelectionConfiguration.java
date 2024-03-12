package de.ume.deidentifhirpipeline.configuration.cohortselection;

import lombok.Getter;
import lombok.Setter;

public class FiremetricsCohortSelectionConfiguration {
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
  private String query;

  @Getter
  @Setter
  private String queryFile;
}
