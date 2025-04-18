package de.ume.deidentifhirpipeline.config.pseudonymization;

import de.ume.deidentifhirpipeline.config.plugin.PluginConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PseudonymizationConfig {
  private boolean                            use = true;
  private DeidentiFHIRPseudonymizationConfig deidentifhir;
  private PluginConfig                       viaPlugin;
}
