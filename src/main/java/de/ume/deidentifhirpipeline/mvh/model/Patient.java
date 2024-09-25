package de.ume.deidentifhirpipeline.mvh.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.ume.deidentifhirpipeline.mvh.model.base.Coding;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
//  @JsonProperty("patientId")
  private String id;
  private Coding gender;
  private String birthDate;
  private String dateOfDeath;
  private String postalCode;
}
