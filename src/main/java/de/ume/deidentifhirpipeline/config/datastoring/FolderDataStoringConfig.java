package de.ume.deidentifhirpipeline.config.datastoring;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderDataStoringConfig {
  private String path;

  public String toString() {
    return String.format("DataStoringConfig(path=%s)", path);
  }
}
