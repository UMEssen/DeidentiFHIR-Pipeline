package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.Coding;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diagnosis {
  private String id;
  private Reference patient;
  private Quantity onsetAge;
  private Coding verificationStatus;
  private List<Coding> categories;
}
