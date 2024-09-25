package de.ume.deidentifhirpipeline.mvh.model;

import de.ume.deidentifhirpipeline.mvh.model.base.Coding;
import de.ume.deidentifhirpipeline.mvh.model.base.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GmfcsStatus {
  private String id;
  private Reference patient;
  private String effectiveDate;
  private Coding value;
}
