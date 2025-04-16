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
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Getter
@Component
public class ImplementationsFactory {

  Environment env;

  private final Map<String, CohortSelection>  cohortSelectionImplementations;
  private final Map<String, GetLastUpdated>   getLastUpdatedImplementations;
  private final Map<String, DataSelection>    dataSelectionImplementations;
  private final Map<String, Pseudonymization> pseudonymizationImplementations;
  private final Map<String, DataStoring>      dataStoringImplementations;
  private final Map<String, SetLastUpdated>   setLastUpdatedImplementations;


  public ImplementationsFactory(
      Environment env,
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

    this.env = env;

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

  public CohortSelection getCohortSelection(ProjectConfig projectConfig) throws Exception {
    Binder binder = Binder.get(env);
    Map<String, Object> cohortSelectionConfig = binder.bind("projects." + projectConfig.getName() + ".cohort-selection", Map.class).orElse(Map.of());

    cohortSelectionConfig.keySet().forEach(s -> log.info("cohort-selection config: " + s));
    if (cohortSelectionConfig.size() > 1)
      throw new Exception("There are multiple cohort-selection configurations. Please check configuration!");
    String cohortString =
        cohortSelectionConfig.keySet().stream().findFirst().orElseThrow(() -> new Exception("Could not find a cohort-selection configuration"));

    return cohortSelectionImplementations.get("cohort-selection." + cohortString);
  }

  public DataSelection getDataSelection(ProjectConfig projectConfig) throws Exception {
    Binder binder = Binder.get(env);
    Map<String, Object> dataSelectionConfig = binder.bind("projects." + projectConfig.getName() + ".data-selection", Map.class).orElse(Map.of());

    dataSelectionConfig.keySet().forEach(s -> log.info("data-selection config: " + s));
    if (dataSelectionConfig.size() > 1)
      throw new Exception("There are multiple cohort-selection configurations. Please check configuration!");
    String dataSelectionString =
        dataSelectionConfig.keySet().stream().findFirst().orElseThrow(() -> new Exception("Could not find a cohort-selection configuration"));

    return dataSelectionImplementations.get("data-selection." + dataSelectionString);
  }

  public DataStoring getDataStoring(ProjectConfig projectConfig) throws Exception {
    Binder binder = Binder.get(env);
    Map<String, Object> dataStoringConfig = binder.bind("projects." + projectConfig.getName() + ".data-storing", Map.class).orElse(Map.of());

    dataStoringConfig.keySet().forEach(s -> log.info("data-selection config: " + s));
    if (dataStoringConfig.size() > 1)
      throw new Exception("There are multiple cohort-selection configurations. Please check configuration!");
    String dataStoringString =
        dataStoringConfig.keySet().stream().findFirst().orElseThrow(() -> new Exception("Could not find a cohort-selection configuration"));

    return dataStoringImplementations.get("data-storing." + dataStoringString);
  }

  public Pseudonymization getPseudonymization(ProjectConfig projectConfig) throws Exception {
    Binder binder = Binder.get(env);
    Map<String, Object> pseudonymizationConfig = binder.bind("projects." + projectConfig.getName() + ".pseudonymization", Map.class).orElse(Map.of());

    pseudonymizationConfig.keySet().forEach(s -> log.info("pseudonymization config: " + s));
    if (pseudonymizationConfig.size() > 1)
      throw new Exception("There are multiple cohort-selection configurations. Please check configuration!");
    String pseudonymizationString =
        pseudonymizationConfig.keySet().stream().findFirst().orElseThrow(() -> new Exception("Could not find a cohort-selection configuration"));

    return pseudonymizationImplementations.get("pseudonymization." + pseudonymizationString);
  }
}
