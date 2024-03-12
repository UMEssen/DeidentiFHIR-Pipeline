package de.ume.deidentifhirpipeline.configuration.pseudonymization;

import lombok.Getter;
import lombok.Setter;

public class PseudonymizationConfiguration {
  @Getter
  @Setter
  private DeidentiFHIRPseudonymizationConfiguration deidentifhir;
}
