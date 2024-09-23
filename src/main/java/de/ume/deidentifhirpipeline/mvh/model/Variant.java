package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.Coding;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Variant {
  private String id;
  private Reference patient;
  private List<Coding> genes;
  private List<Coding> localization;
  private Coding gDNAChange;
  private Coding cDNAChange;
  private Coding proteinChange;
  private Coding acmgClass;
  private List<AcmgCriterion> acmgCriteria;
  private Coding zygosity;
  private Coding segregationAnalysis;
  private Coding modeOfInheritance;
  private Coding significance;
  private ClinVar clinVarID;
  // publications...
}
