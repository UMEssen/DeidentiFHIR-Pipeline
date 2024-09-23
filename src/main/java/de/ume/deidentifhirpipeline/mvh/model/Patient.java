package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.Coding;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
  private String id;
  private Coding gender;
  private String birthDate;
  private String dateOfDeath;
  private String postalCode;
}
