package de.ume.deidentifhirpipeline.configuration.dataselection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSelectionConfiguration {
  private FhirServerDataSelectionConfiguration fhirServer;
  private FhirCollectorDataSelectionConfiguration fhirCollector;
  private FiremetricsDataSelectionConfiguration firemetrics;
  private DummyDataSelectionConfiguration dummy;
}
