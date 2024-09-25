package de.ume.deidentifhirpipeline.mvh.model;

import de.ume.deidentifhirpipeline.mvh.model.base.Coding;
import de.ume.deidentifhirpipeline.mvh.model.base.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
