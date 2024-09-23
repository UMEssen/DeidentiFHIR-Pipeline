package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarePlan {
  private String id;
  private Reference patient;
  private String issuedOn;
  private boolean sequencingRequested;
  private TherapyRecommendation therapyRecommendations;
  private StudyEnrollmentRecommendation studyEnrollmentRecommendation;
  private ClinicalManagementRecommendation clinicalManagementRecommendation;
  private String notes;
}
