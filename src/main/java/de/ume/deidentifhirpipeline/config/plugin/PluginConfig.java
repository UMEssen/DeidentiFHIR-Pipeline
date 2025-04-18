package de.ume.deidentifhirpipeline.config.plugin;

import java.util.Map;
import lombok.Data;

@Data
public class PluginConfig {
  private String              implementation;
  private Map<String, Object> config;
}
