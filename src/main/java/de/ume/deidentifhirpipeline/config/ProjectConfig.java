package de.ume.deidentifhirpipeline.config;

import de.ume.deidentifhirpipeline.config.cohortselection.CohortSelectionConfig;
import de.ume.deidentifhirpipeline.config.dataselection.DataSelectionConfig;
import de.ume.deidentifhirpipeline.config.datastoring.DataStoringConfig;
import de.ume.deidentifhirpipeline.config.lastupdated.LastUpdatedConfig;
import de.ume.deidentifhirpipeline.config.pseudonymization.PseudonymizationConfig;
import de.ume.deidentifhirpipeline.transfer.ImplementationsFactory;
import de.ume.deidentifhirpipeline.transfer.cohortselection.*;
import de.ume.deidentifhirpipeline.transfer.dataselection.*;
import de.ume.deidentifhirpipeline.transfer.datastoring.DataStoring;
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
public class ProjectConfig {
  private int parallelism = 1;

  @Setter private LastUpdatedConfig      lastUpdated;
  @Setter private CohortSelectionConfig  cohortSelection;
  @Setter private DataSelectionConfig    dataSelection;
  @Setter private PseudonymizationConfig pseudonymization;
  @Setter private DataStoringConfig      dataStoring;

  @Setter private Optional<GetLastUpdated> getLastUpdatedImpl = Optional.empty();
  @Setter private Optional<SetLastUpdated> setLastUpdatedImpl = Optional.empty();
  @Setter private CohortSelection          cohortSelectionImpl;
  @Setter private DataSelection            dataSelectionImpl;
  @Setter private Pseudonymization         pseudonymizationImpl;
  @Setter private DataStoring              dataStoringImpl;

  public ProjectConfig(
      int parallelism,
      LastUpdatedConfig lastUpdated,
      CohortSelectionConfig cohortSelection,
      DataSelectionConfig dataSelection,
      PseudonymizationConfig pseudonymization,
      DataStoringConfig dataStoring) throws Exception {

    this.parallelism      = parallelism;
    this.lastUpdated      = lastUpdated;
    this.cohortSelection  = cohortSelection;
    this.dataSelection    = dataSelection;
    this.pseudonymization = pseudonymization;
    this.dataStoring      = dataStoring;

    if (parallelism <= 0)
      this.parallelism = 1;
    // if (lastUpdated != null && (lastUpdated.getHashmap() != null || lastUpdated.getGpas() != null)) {
    // getLastUpdatedImpl = Optional.of(new GetLastUpdatedImpl(lastUpdated));
    // setLastUpdatedImpl = Optional.of(new SetLastUpdatedImpl(lastUpdated));
    // }
    // @formatter:off
    // Cohort Selection
//    if (cohortSelection != null && cohortSelection.getGics() != null)           cohortSelectionImpl   = new GicsCohortSelection();
//    if (cohortSelection != null && cohortSelection.getViaIds() != null)         cohortSelectionImpl   = new IdCohortSelection();
//    if (cohortSelection != null && cohortSelection.getViaFile() != null)        cohortSelectionImpl   = new FileCohortSelection();
//    if (cohortSelection != null && cohortSelection.getFiremetrics() != null)    cohortSelectionImpl   = new FiremetricsCohortSelection();
//    // Data Selection
//    if (dataSelection != null && dataSelection.getFhirServer() != null)         dataSelectionImpl     = new FhirServerDataSelection();
//    if (dataSelection != null && dataSelection.getFhirCollector() != null)      dataSelectionImpl     = new FhirCollectorDataSelection();
//    if (dataSelection != null && dataSelection.getFiremetrics() != null)        dataSelectionImpl     = new FiremetricsDataSelection();
//    // Pseudonymization
//    if (pseudonymization != null && pseudonymization.getDeidentifhir() != null) pseudonymizationImpl  = new DeidentiFHIRPseudonymization();
//    if (pseudonymization != null && pseudonymization.isUse() == false)          pseudonymizationImpl  = new NoPseudonymization();
//    // Data Storing
//    if (dataStoring != null && dataStoring.getFhirServer() != null)             dataStoringImpl       = new FhirServerDataStoring();
//    if (dataStoring != null && dataStoring.getFiremetrics() != null)            dataStoringImpl       = new FiremetricsDataStoring();
//    if (dataStoring != null && dataStoring.getFolder() != null)                 dataStoringImpl       = new FolderDataStoring();
    // @formatter:on
  }

  public void setup(ImplementationsFactory implementationsFactory) throws Exception {
    this.setCohortSelectionImpl(implementationsFactory.getCohortSelection(this));
    this.setDataSelectionImpl(implementationsFactory.getDataSelection(this));
    this.setPseudonymizationImpl(implementationsFactory.getPseudonymization(this));
    this.setDataStoringImpl(implementationsFactory.getDataStoring(this));
    this.validate();
  }

  public void validate() throws Exception {
    if (getLastUpdatedImpl.isEmpty() || setLastUpdatedImpl.isEmpty())
      log.info("LastUpdated implementation not configured.");
    // @formatter:off
    if (cohortSelectionImpl   == null) throw new Exception("No CohortSelection implementation found. Check configuration.");
    if (dataSelectionImpl     == null) throw new Exception("No DataSelection implementation found. Check configuration.");
    if (pseudonymizationImpl  == null) throw new Exception("No Pseudonymization implementation found. Check configuration.");
    if (dataStoringImpl       == null) throw new Exception("No DataSelection implementation found. Check configuration.");
    // @formatter:on
  }

  public ProjectConfig apply(ProjectConfig projectConfig) throws Exception {
    if (projectConfig == null) {
      return this;
    }
    int configuredParallelism = projectConfig.getParallelism() != 0 ? projectConfig.getParallelism() : this.parallelism;

    // @formatter:off
    LastUpdatedConfig lastUpdatedConfig           = projectConfig.getLastUpdated()      != null ? projectConfig.getLastUpdated()      : this.lastUpdated;
    CohortSelectionConfig cohortSelectionConfig   = projectConfig.getCohortSelection()  != null ? projectConfig.getCohortSelection()  : this.cohortSelection;
    DataSelectionConfig dataSelectionConfig       = projectConfig.getDataSelection()    != null ? projectConfig.getDataSelection()    : this.dataSelection;
    PseudonymizationConfig pseudonymizationConfig = projectConfig.getPseudonymization() != null ? projectConfig.getPseudonymization() : this.pseudonymization;
    DataStoringConfig dataStoringConfig           = projectConfig.getDataStoring()      != null ? projectConfig.getDataStoring()      : this.dataStoring;
    // @formatter:on

    return new ProjectConfig(
        configuredParallelism,
        lastUpdatedConfig,
        cohortSelectionConfig,
        dataSelectionConfig,
        pseudonymizationConfig,
        dataStoringConfig);
  }
}
