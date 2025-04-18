package de.ume.deidentifhirpipeline.config.dataselection;

import de.ume.deidentifhirpipeline.config.plugin.PluginConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSelectionConfig {
  private FhirServerDataSelectionConfig    fhirServer;
  private FhirCollectorDataSelectionConfig fhirCollector;
  private FiremetricsDataSelectionConfig   firemetrics;
  private PluginConfig                     viaPlugin;
}
