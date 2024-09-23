package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.Coding;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {
  private String id;
  private Reference patient;
  private Reference basedOn;
  private String recordedOn;
  private Coding category;
  private List<Coding> medication;
  private Period period;
  private String notes;
}
