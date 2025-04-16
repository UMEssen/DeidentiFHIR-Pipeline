package de.ume.deidentifhirpipeline.transfer;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.cohortselection.CohortSelection;
import de.ume.deidentifhirpipeline.transfer.dataselection.DataSelection;
import de.ume.deidentifhirpipeline.transfer.datastoring.DataStoring;
import de.ume.deidentifhirpipeline.transfer.lastupdated.GetLastUpdated;
import de.ume.deidentifhirpipeline.transfer.lastupdated.SetLastUpdated;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.Pseudonymization;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Getter
@Component
public class ImplementationsFactory {

  private final Map<String, CohortSelection>  cohortSelectionImplementations;
  private final Map<String, GetLastUpdated>   getLastUpdatedImplementations;
  private final Map<String, DataSelection>    dataSelectionImplementations;
  private final Map<String, Pseudonymization> pseudonymizationImplementations;
  private final Map<String, DataStoring>      dataStoringImplementations;
  private final Map<String, SetLastUpdated>   setLastUpdatedImplementations;


  public ImplementationsFactory(
      Map<String, CohortSelection> cohortSelectionImplementations,
      Map<String, GetLastUpdated> getLastUpdatedImplementations,
      Map<String, DataSelection> dataSelectionImplementations,
      Map<String, Pseudonymization> pseudonymizationImplementations,
      Map<String, DataStoring> dataStoringImplementations,
      Map<String, SetLastUpdated> setLastUpdatedImplementations) {
    this.cohortSelectionImplementations  = cohortSelectionImplementations;
    this.getLastUpdatedImplementations   = getLastUpdatedImplementations;
    this.dataSelectionImplementations    = dataSelectionImplementations;
    this.pseudonymizationImplementations = pseudonymizationImplementations;
    this.dataStoringImplementations      = dataStoringImplementations;
    this.setLastUpdatedImplementations   = setLastUpdatedImplementations;

    log.info("Found implementations: ");
    cohortSelectionImplementations.keySet().forEach(s -> log.info("Cohort Selection: " + s));
    getLastUpdatedImplementations.keySet().forEach(s -> log.info("GetLastUpdated: " + s));
    dataSelectionImplementations.keySet().forEach(s -> log.info("Data Selection: " + s));
    pseudonymizationImplementations.keySet().forEach(s -> log.info("Pseudonymization: " + s));
    dataStoringImplementations.keySet().forEach(s -> log.info("Data Storing: " + s));
    setLastUpdatedImplementations.keySet().forEach(s -> log.info("Set Last Updated: " + s));
  }


  public GetLastUpdated getLastUpdated(ProjectConfig projectConfig) {
    GetLastUpdated getLastUpdated;
    if (projectConfig.getLastUpdated() != null) {
      getLastUpdated = getLastUpdatedImplementations.get("get-last-updated");
    } else {
      getLastUpdated = null;
    }
    return getLastUpdated;
  }

  public SetLastUpdated getSetLastUpdated(ProjectConfig projectConfig) {
    SetLastUpdated setLastUpdated;
    if (projectConfig.getLastUpdated() != null) {
      setLastUpdated = setLastUpdatedImplementations.get("set-last-updated");
    } else {
      setLastUpdated = null;
    }
    return setLastUpdated;
  }

  public CohortSelection getCohortSelection(ProjectConfig projectConfig) {
    CohortSelection cohortselection;
    if (projectConfig.getCohortSelection().getViaIds() != null) {
      cohortselection = cohortSelectionImplementations.get("cohort-selection.via-ids");
    } else if (projectConfig.getCohortSelection().getViaFile() != null) {
      cohortselection = cohortSelectionImplementations.get("cohort-selection.via-file");
    } else if (projectConfig.getCohortSelection().getGics() != null) {
      cohortselection = cohortSelectionImplementations.get("cohort-selection.gics");
    } else if (projectConfig.getCohortSelection().getFiremetrics() != null) {
      cohortselection = cohortSelectionImplementations.get("cohort-selection.firemetrics");
    } else {
      cohortselection = null;
    }
    return cohortselection;
  }

  public DataSelection getDataSelection(ProjectConfig projectConfig) {
    DataSelection dataSelection;
    if (projectConfig.getDataSelection().getFhirServer() != null) {
      dataSelection = dataSelectionImplementations.get("data-selection.fhir-server");
    } else if (projectConfig.getDataSelection().getFhirCollector() != null) {
      dataSelection = dataSelectionImplementations.get("data-selection.fhir-collector");
    } else if (projectConfig.getDataSelection().getFiremetrics() != null) {
      dataSelection = dataSelectionImplementations.get("data-selection.firemetrics");
    } else {
      dataSelection = null;
    }
    return dataSelection;
  }

  public DataStoring getDataStoring(ProjectConfig projectConfig) {
    DataStoring dataStoring;
    if (projectConfig.getDataStoring().getFhirServer() != null) {
      dataStoring = dataStoringImplementations.get("data-storing.fhir-server");
    } else if (projectConfig.getDataStoring().getFolder() != null) {
      dataStoring = dataStoringImplementations.get("data-storing.folder");
    } else if (projectConfig.getDataStoring().getFiremetrics() != null) {
      dataStoring = dataStoringImplementations.get("data-storing.firemetrics");

    } else {
      dataStoring = null;
    }
    return dataStoring;
  }

  public Pseudonymization getPseudonymization(ProjectConfig projectConfig) {
    Pseudonymization pseudonymization;
    if (projectConfig.getPseudonymization().getDeidentifhir() != null) {
      pseudonymization = pseudonymizationImplementations.get("pseudonymization.deidentifhir");
    } else {
      pseudonymization = pseudonymizationImplementations.get("pseudonymization.none");
    }
    return pseudonymization;
  }
}
