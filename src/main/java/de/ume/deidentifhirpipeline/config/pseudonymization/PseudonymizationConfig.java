package de.ume.deidentifhirpipeline.config.pseudonymization;

import de.ume.deidentifhirpipeline.config.plugin.PluginConfig;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.NoPseudonymization;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Semaphore;

@Getter
@Setter
public class PseudonymizationConfig {
  private int                                parallelism = 16;
  private DeidentiFHIRPseudonymizationConfig deidentifhir;
  private String                             none;            // Dummy: No config for NoPseudonymization
  private PluginConfig                       viaPlugin;
  private Semaphore                          semaphore;
}
