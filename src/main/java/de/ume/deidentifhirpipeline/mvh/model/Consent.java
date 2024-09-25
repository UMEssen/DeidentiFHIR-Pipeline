package de.ume.deidentifhirpipeline.mvh.model;

import de.ume.deidentifhirpipeline.mvh.model.base.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consent {
  private Reference patient;
}
