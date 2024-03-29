package de.ume.deidentifhirpipeline.configuration.cohortselection;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IdCohortSelectionConfiguration {
  List<String> ids;
}
