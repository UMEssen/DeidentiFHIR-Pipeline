package de.ume.deidentifhirpipeline.config.cohortselection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CsvCohortSelectionConfig {
  String  path;
  String  delimiter;
  String  columnName;
  Integer columnNumber;
}
