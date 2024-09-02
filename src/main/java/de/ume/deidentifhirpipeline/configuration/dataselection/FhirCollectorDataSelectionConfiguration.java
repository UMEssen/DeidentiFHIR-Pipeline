package de.ume.deidentifhirpipeline.configuration.dataselection;

import de.ume.deidentifhirpipeline.configuration.auth.BasicAuthConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FhirCollectorDataSelectionConfiguration {
  private String configurationFile;
}
