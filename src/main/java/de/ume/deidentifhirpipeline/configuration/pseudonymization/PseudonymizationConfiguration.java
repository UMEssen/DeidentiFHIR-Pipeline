package de.ume.deidentifhirpipeline.configuration.pseudonymization;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PseudonymizationConfiguration {
  private boolean use = true;
  private DeidentiFHIRPseudonymizationConfiguration deidentifhir;
}
