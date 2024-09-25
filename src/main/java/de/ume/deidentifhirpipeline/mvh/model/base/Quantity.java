package de.ume.deidentifhirpipeline.mvh.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quantity {
  private String value;
  private String unit;
}
