package de.ume.deidentifhirpipeline.config.datastoring;

import de.ume.deidentifhirpipeline.config.plugin.PluginConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataStoringConfig {
  private FhirServerDataStoringConfig  fhirServer;
  private FiremetricsDataStoringConfig firemetrics;
  private FolderDataStoringConfig      folder;
  private PluginConfig                 viaPlugin;
}
