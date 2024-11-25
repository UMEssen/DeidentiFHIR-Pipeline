package de.ume.deidentifhirpipeline.config.datastoring;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataStoringConfig {
  private FhirServerDataStoringConfig fhirServer;
  private FiremetricsDataStoringConfig firemetrics;
  private FolderDataStoringConfig folder;
}
