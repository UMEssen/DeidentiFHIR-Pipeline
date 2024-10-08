package de.ume.deidentifhirpipeline.configuration;

import de.ume.deidentifhirpipeline.configuration.cohortselection.CohortSelectionConfiguration;
import de.ume.deidentifhirpipeline.configuration.dataselection.DataSelectionConfiguration;
import de.ume.deidentifhirpipeline.configuration.datastoring.DataStoringConfiguration;
import de.ume.deidentifhirpipeline.configuration.lastupdated.LastUpdatedConfiguration;
import de.ume.deidentifhirpipeline.configuration.pseudonymization.PseudonymizationConfiguration;
import de.ume.deidentifhirpipeline.transfer.cohortselection.CohortSelection;
import de.ume.deidentifhirpipeline.transfer.cohortselection.FiremetricsCohortSelection;
import de.ume.deidentifhirpipeline.transfer.cohortselection.GicsCohortSelection;
import de.ume.deidentifhirpipeline.transfer.cohortselection.IdCohortSelection;
import de.ume.deidentifhirpipeline.transfer.dataselection.*;
import de.ume.deidentifhirpipeline.transfer.datastoring.DataStoring;
import de.ume.deidentifhirpipeline.transfer.datastoring.FhirServerDataStoring;
import de.ume.deidentifhirpipeline.transfer.datastoring.FiremetricsDataStoring;
import de.ume.deidentifhirpipeline.transfer.datastoring.FolderDataStoring;
import de.ume.deidentifhirpipeline.transfer.lastupdated.GetLastUpdated;
import de.ume.deidentifhirpipeline.transfer.lastupdated.GetLastUpdatedImpl;
import de.ume.deidentifhirpipeline.transfer.lastupdated.SetLastUpdatedImpl;
import de.ume.deidentifhirpipeline.transfer.lastupdated.SetLastUpdated;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.DeidentiFHIRPseudonymization;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.NoPseudonymization;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.Pseudonymization;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Getter
public class ProjectConfiguration {
  private int parallelism = 1;
  @Setter
  private LastUpdatedConfiguration lastUpdated;
  @Setter
  private CohortSelectionConfiguration cohortSelection;
  @Setter
  private DataSelectionConfiguration dataSelection;
  @Setter
  private PseudonymizationConfiguration pseudonymization;
  @Setter
  private DataStoringConfiguration dataStoring;

  private Optional<GetLastUpdated> getLastUpdatedImpl = Optional.empty();
  private Optional<SetLastUpdated> setLastUpdatedImpl = Optional.empty();
  private CohortSelection cohortSelectionImpl;
  private DataSelection dataSelectionImpl;
  private Pseudonymization pseudonymizationImpl;
  private DataStoring dataStoringImpl;

  public ProjectConfiguration(int parallelism,
      LastUpdatedConfiguration lastUpdated,
      CohortSelectionConfiguration cohortSelection,
      DataSelectionConfiguration dataSelection,
      PseudonymizationConfiguration pseudonymization,
      DataStoringConfiguration dataStoring) throws Exception {

    this.parallelism = parallelism;
    this.lastUpdated = lastUpdated;
    this.cohortSelection = cohortSelection;
    this.dataSelection = dataSelection;
    this.pseudonymization = pseudonymization;
    this.dataStoring = dataStoring;

    if( parallelism <= 0 ) this.parallelism = 1;
    if( lastUpdated != null && (lastUpdated.getHashmap() != null || lastUpdated.getGpas() != null )) {
      getLastUpdatedImpl = Optional.of(new GetLastUpdatedImpl(lastUpdated));
      setLastUpdatedImpl = Optional.of(new SetLastUpdatedImpl(lastUpdated));
    }
    if( cohortSelection  != null && cohortSelection.getGics() != null )                 cohortSelectionImpl  = new GicsCohortSelection();
    if( cohortSelection  != null && cohortSelection.getViaIds() != null )               cohortSelectionImpl  = new IdCohortSelection();
    if( cohortSelection  != null && cohortSelection.getFiremetrics() != null )          cohortSelectionImpl  = new FiremetricsCohortSelection();
    if( dataSelection    != null && dataSelection.getFhirServer() != null )             dataSelectionImpl    = new FhirServerDataSelection();
    if( dataSelection    != null && dataSelection.getFhirCollector() != null )          dataSelectionImpl    = new FhirCollectorDataSelection();
    if( dataSelection    != null && dataSelection.getFiremetrics() != null )            dataSelectionImpl    = new FiremetricsDataSelection();
    if( pseudonymization != null && pseudonymization.getDeidentifhir() != null )        pseudonymizationImpl = new DeidentiFHIRPseudonymization();
    if( pseudonymization != null && pseudonymization.isUse() == false )                 pseudonymizationImpl = new NoPseudonymization();
    if( dataStoring      != null && dataStoring.getFhirServer() != null )               dataStoringImpl      = new FhirServerDataStoring();
    if( dataStoring      != null && dataStoring.getFiremetrics() != null )              dataStoringImpl      = new FiremetricsDataStoring();
    if( dataStoring      != null && dataStoring.getFolder() != null )                   dataStoringImpl      = new FolderDataStoring();

  }

  public void validate() throws Exception {
    if( getLastUpdatedImpl.isEmpty() || setLastUpdatedImpl.isEmpty() ) log.info("LastUpdated implementation not configured.");
    if( cohortSelectionImpl == null )   throw new Exception("No CohortSelection implementation found. Check configuration.");
    if( dataSelectionImpl == null )     throw new Exception("No DataSelection implementation found. Check configuration.");
    if( pseudonymizationImpl == null )  throw new Exception("No Pseudonymization implementation found. Check configuration.");
    if( dataStoringImpl == null )       throw new Exception("No DataSelection implementation found. Check configuration.");
  }

  public ProjectConfiguration apply(ProjectConfiguration projectConfiguration) throws Exception {
    if( projectConfiguration == null ) {
      return this;
    }
    int configuredParallelism = projectConfiguration.getParallelism() != 0 ? projectConfiguration.getParallelism() : this.parallelism;

    LastUpdatedConfiguration lastUpdatedConfiguration =
        projectConfiguration.getLastUpdated() != null ? projectConfiguration.getLastUpdated() : this.lastUpdated;

    CohortSelectionConfiguration cohortSelectionConfiguration =
        projectConfiguration.getCohortSelection() != null ? projectConfiguration.getCohortSelection() : this.cohortSelection;

    DataSelectionConfiguration dataSelectionConfiguration =
        projectConfiguration.getDataSelection() != null ? projectConfiguration.getDataSelection() : this.dataSelection;

    PseudonymizationConfiguration pseudonymizationConfiguration =
        projectConfiguration.getPseudonymization() != null ? projectConfiguration.getPseudonymization() : this.pseudonymization;

    DataStoringConfiguration dataStoringConfiguration =
        projectConfiguration.getDataStoring() != null ? projectConfiguration.getDataStoring(): this.dataStoring;

    return new ProjectConfiguration(
        configuredParallelism,
        lastUpdatedConfiguration,
        cohortSelectionConfiguration,
        dataSelectionConfiguration,
        pseudonymizationConfiguration,
        dataStoringConfiguration
    );

  }

}
