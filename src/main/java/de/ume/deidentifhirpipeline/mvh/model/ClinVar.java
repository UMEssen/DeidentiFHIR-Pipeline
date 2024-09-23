package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinVar {
  private String value;
  private String system;
}
