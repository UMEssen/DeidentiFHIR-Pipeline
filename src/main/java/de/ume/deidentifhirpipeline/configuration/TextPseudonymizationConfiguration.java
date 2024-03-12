package de.ume.deidentifhirpipeline.configuration;

import de.ume.deidentifhirpipeline.configuration.service.GpasServiceConfiguration;
import de.ume.deidentifhirpipeline.configuration.service.InceptionServiceConfiguration;
import lombok.Getter;
import lombok.Setter;

public class TextPseudonymizationConfiguration {
  @Getter
  @Setter
  private GpasServiceConfiguration gpas;
  @Getter
  @Setter
  private InceptionServiceConfiguration inception;
}
