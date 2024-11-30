package de.ume.deidentifhirpipeline.config.dataselection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiremetricsDataSelectionConfig {
  private String host;
  private int    port;
  private String database;
  private String user;
  private String password;
  private String query;
  private String queryFile;
  private String queryIdPlaceholderString;
  private String queryLastUpdatedPlaceholderString;

}
