package de.ume.deidentifhirpipeline.configuration.datastoring;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderDataStoringConfiguration {
  private String path;

  public String toString() {
    return String.format("DataStoringConfiguration(path=%s)", path);
  }
}
