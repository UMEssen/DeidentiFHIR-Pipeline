package de.ume.deidentifhirpipeline.configuration.cohortselection;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class IdCohortSelectionConfiguration {
  @Getter
  @Setter
  List<String> ids;
}
