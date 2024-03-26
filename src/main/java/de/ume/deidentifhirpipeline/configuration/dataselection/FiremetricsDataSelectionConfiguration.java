package de.ume.deidentifhirpipeline.configuration.dataselection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiremetricsDataSelectionConfiguration {
  private String host;
  private int port;
  private String database;
  private String user;
  private String password;
  private String query;
  private String queryFile;
  private String queryIdPlaceholderString;
  private String queryLastUpdatedPlaceholderString;

}
