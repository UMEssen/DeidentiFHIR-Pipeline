package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.Coding;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HpoTerm {
  private String id;
  private Reference patient;
  private String onsetDate;
  private Coding value;
  private List<Status> statusHistory;
}
