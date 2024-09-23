package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.Coding;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GmfcsStatus {
  private String id;
  private Reference patient;
  private String effectiveDate;
  private Coding value;
}
