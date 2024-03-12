package de.ume.deidentifhirpipeline.configuration.dataselection;

import de.ume.deidentifhirpipeline.configuration.auth.BasicAuthConfiguration;
import lombok.Getter;
import lombok.Setter;

public class DummyDataSelectionConfiguration {
  @Getter
  @Setter
  private String url;
  @Getter
  @Setter
  private BasicAuthConfiguration basic;
}
