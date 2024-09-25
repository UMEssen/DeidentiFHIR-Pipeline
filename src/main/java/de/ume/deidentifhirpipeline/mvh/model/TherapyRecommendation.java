package de.ume.deidentifhirpipeline.mvh.model;

import de.ume.deidentifhirpipeline.mvh.model.base.Coding;
import de.ume.deidentifhirpipeline.mvh.model.base.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TherapyRecommendation {
  private String id;
  private Reference patient;
  private String issuedOn;
  private Coding category;
  private List<Coding> medication;
  private Reference supportingVariants;
}
