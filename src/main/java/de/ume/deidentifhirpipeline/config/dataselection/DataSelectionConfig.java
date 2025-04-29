package de.ume.deidentifhirpipeline.config.dataselection;

import de.ume.deidentifhirpipeline.config.plugin.PluginConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Semaphore;

@Getter
@Setter
public class DataSelectionConfig {
  private int                              parallelism = 16;
  private FhirServerDataSelectionConfig    fhirServer;
  private FhirCollectorDataSelectionConfig fhirCollector;
  private FiremetricsDataSelectionConfig   firemetrics;
  private PluginConfig                     viaPlugin;
  private Semaphore                        semaphore;
}
