package de.ume.deidentifhirpipeline.mvh.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Period {
  private String start;
  private String end;
}
