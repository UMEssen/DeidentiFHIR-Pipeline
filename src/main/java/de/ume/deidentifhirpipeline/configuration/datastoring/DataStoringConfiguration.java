package de.ume.deidentifhirpipeline.configuration.datastoring;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataStoringConfiguration {
  private FhirServerDataStoringConfiguration fhirServer;
  private FiremetricsDataStoringConfiguration firemetrics;
  private FolderDataStoringConfiguration folder;
}
