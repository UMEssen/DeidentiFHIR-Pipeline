package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Record {
  private Patient patient;
  private Consent consent;
  private EpisodeOfCare episodeOfCare;
  private Diagnosis diagnosis;
  private List<GmfcsStatus> gmfcsStatus;
  private Hospitalization hospitalization;
  private List<HpoTerm> hpoTerms;
  private List<NgsReport> ngsReports;
  private List<CarePlan> carePlans;
  private List<Therapy> therapies;
}
