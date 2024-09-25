package de.ume.deidentifhirpipeline.mvh.model;

import de.ume.deidentifhirpipeline.mvh.model.base.Coding;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {
  private Coding status;
  private String date;
}
