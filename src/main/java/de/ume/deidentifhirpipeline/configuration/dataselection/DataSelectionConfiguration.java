package de.ume.deidentifhirpipeline.configuration.dataselection;

import lombok.Getter;
import lombok.Setter;

public class DataSelectionConfiguration {
  @Getter
  @Setter
  private FhirServerDataSelectionConfiguration fhirServer;
  @Getter
  @Setter
  private FiremetricsDataSelectionConfiguration firemetrics;
  @Getter
  @Setter
  private DummyDataSelectionConfiguration dummy;
}
