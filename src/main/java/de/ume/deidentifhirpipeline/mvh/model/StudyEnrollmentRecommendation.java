package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.Coding;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyEnrollmentRecommendation {
  private String id;
  private Reference patient;
  private String issuedOn;
  private Reference supportingVariants;
  private List<Coding> studies;
}
