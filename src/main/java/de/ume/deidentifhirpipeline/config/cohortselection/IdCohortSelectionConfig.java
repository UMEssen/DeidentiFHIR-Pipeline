package de.ume.deidentifhirpipeline.config.cohortselection;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IdCohortSelectionConfig {
  List<String> ids;
}
