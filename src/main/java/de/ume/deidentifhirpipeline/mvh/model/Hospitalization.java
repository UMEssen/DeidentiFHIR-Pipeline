package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hospitalization {
  private String id;
  private Reference patient;
  private Period period;
  private Quantity duration;
}
