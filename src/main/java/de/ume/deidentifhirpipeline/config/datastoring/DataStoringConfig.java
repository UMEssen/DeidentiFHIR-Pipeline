package de.ume.deidentifhirpipeline.config.datastoring;

import de.ume.deidentifhirpipeline.config.plugin.PluginConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Semaphore;

@Getter
@Setter
public class DataStoringConfig {
  private int                          parallelism = 16;
  private FhirServerDataStoringConfig  fhirServer;
  private FiremetricsDataStoringConfig firemetrics;
  private FolderDataStoringConfig      folder;
  private PluginConfig                 viaPlugin;
  private Semaphore                    semaphore;
}
