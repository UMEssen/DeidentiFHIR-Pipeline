package de.ume.deidentifhirpipeline.config.cohortselection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileCohortSelectionConfig {
  String path;
  String delimiter;
}
