package de.ume.deidentifhirpipeline.configuration;

import de.ume.deidentifhirpipeline.configuration.cohortselection.CohortSelectionConfiguration;
import de.ume.deidentifhirpipeline.configuration.dataselection.DataSelectionConfiguration;
import de.ume.deidentifhirpipeline.configuration.datastoring.DataStoringConfiguration;
import de.ume.deidentifhirpipeline.configuration.pseudonymization.PseudonymizationConfiguration;
import de.ume.deidentifhirpipeline.transfer.cohortselection.CohortSelection;
import de.ume.deidentifhirpipeline.transfer.cohortselection.FiremetricsCohortSelection;
import de.ume.deidentifhirpipeline.transfer.cohortselection.GicsCohortSelection;
import de.ume.deidentifhirpipeline.transfer.cohortselection.IdCohortSelection;
import de.ume.deidentifhirpipeline.transfer.dataselection.DataSelection;
import de.ume.deidentifhirpipeline.transfer.dataselection.DummyDataSelection;
import de.ume.deidentifhirpipeline.transfer.dataselection.FhirServerDataSelection;
import de.ume.deidentifhirpipeline.transfer.dataselection.FiremetricsDataSelection;
import de.ume.deidentifhirpipeline.transfer.datastoring.DataStoring;
import de.ume.deidentifhirpipeline.transfer.datastoring.FhirServerDataStoring;
import de.ume.deidentifhirpipeline.transfer.datastoring.FiremetricsDataStoring;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.DeidentiFHIRPseudonymization;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.Pseudonymization;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ProjectConfiguration {
  private int parallelism = 1;
  @Setter
  private CohortSelectionConfiguration cohortSelection;
  @Setter
  private DataSelectionConfiguration dataSelection;
  @Setter
  private PseudonymizationConfiguration pseudonymization;
  @Setter
  private DataStoringConfiguration dataStoring;

  private CohortSelection cohortSelectionImpl;
  private DataSelection dataSelectionImpl;
  private Pseudonymization pseudonymizationImpl;
  private DataStoring dataStoringImpl;

  public ProjectConfiguration(int parallelism,
      CohortSelectionConfiguration cohortSelection,
      DataSelectionConfiguration dataSelection,
      PseudonymizationConfiguration pseudonymization,
      DataStoringConfiguration dataStoring) throws Exception {
    this.parallelism = parallelism;
    this.cohortSelection = cohortSelection;
    this.dataSelection = dataSelection;
    this.pseudonymization = pseudonymization;
    this.dataStoring = dataStoring;

    if( parallelism <= 0 ) this.parallelism = 1;
    if( cohortSelection != null && cohortSelection.getGics() != null )            cohortSelectionImpl = new GicsCohortSelection(cohortSelection.getGics());
    if( cohortSelection != null && cohortSelection.getViaIds() != null )          cohortSelectionImpl = new IdCohortSelection(cohortSelection.getViaIds());
    if( cohortSelection != null && cohortSelection.getFiremetrics() != null )     cohortSelectionImpl = new FiremetricsCohortSelection(cohortSelection.getFiremetrics());
    if( dataSelection != null && dataSelection.getFhirServer() != null )          dataSelectionImpl = new FhirServerDataSelection(dataSelection.getFhirServer());
    if( dataSelection != null && dataSelection.getFiremetrics() != null )         dataSelectionImpl = new FiremetricsDataSelection(dataSelection.getFiremetrics());
    if( dataSelection != null && dataSelection.getDummy() != null )               dataSelectionImpl = new DummyDataSelection(dataSelection.getDummy());
    if( pseudonymization != null && pseudonymization.getDeidentifhir() != null )  pseudonymizationImpl = new DeidentiFHIRPseudonymization(pseudonymization.getDeidentifhir());
    if( dataStoring != null && dataStoring.getFhirServer() != null )              dataStoringImpl = new FhirServerDataStoring(dataStoring.getFhirServer());
    if( dataStoring != null && dataStoring.getFiremetrics() != null )             dataStoringImpl = new FiremetricsDataStoring(dataStoring.getFiremetrics());

  }

  public void validate() throws Exception {
    if( cohortSelectionImpl == null )   throw new Exception("No CohortSelection implementation found. Check configuration.");
    if( dataSelectionImpl == null )     throw new Exception("No DataSelection implementation found. Check configuration.");
    if( pseudonymizationImpl == null )  throw new Exception("No Pseudonymization implementation found. Check configuration.");
    if( dataStoringImpl == null )       throw new Exception("No DataSelection implementation found. Check configuration.");
  }

  public ProjectConfiguration apply(ProjectConfiguration projectConfiguration) throws Exception {
    if( projectConfiguration == null ) {
      return this;
    }
    int paralellism = projectConfiguration.getParallelism() != 0 ? projectConfiguration.getParallelism() : this.parallelism;
    CohortSelectionConfiguration cohortSelectionConfiguration =
        projectConfiguration.getCohortSelection() != null ? projectConfiguration.getCohortSelection() : this.cohortSelection;

    DataSelectionConfiguration dataSelectionConfiguration =
        projectConfiguration.getDataSelection() != null ? projectConfiguration.getDataSelection() : this.dataSelection;


    PseudonymizationConfiguration pseudonymizationConfiguration =
        projectConfiguration.getPseudonymization() != null ? projectConfiguration.getPseudonymization() : this.pseudonymization;


    DataStoringConfiguration dataStoringConfiguration =
        projectConfiguration.getDataStoring() != null ? projectConfiguration.getDataStoring(): this.dataStoring;

    return new ProjectConfiguration(
        parallelism,
        cohortSelectionConfiguration,
        dataSelectionConfiguration,
        pseudonymizationConfiguration,
        dataStoringConfiguration
    );

  }

}
