package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.Coding;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NgsReport {
  private String id;
  private Reference patient;
  private Reference performingLab;
  private String recordedOn;
  private Coding sequencingType;
  private Coding familyControls;
  // ... (not finished)
}
