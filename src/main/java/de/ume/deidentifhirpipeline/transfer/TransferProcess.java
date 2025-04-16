package de.ume.deidentifhirpipeline.transfer;

import de.ume.deidentifhirpipeline.api.data.Transfer;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.cohortselection.CohortSelectionInterface;
import de.ume.deidentifhirpipeline.transfer.dataselection.DataSelectionInterface;
import de.ume.deidentifhirpipeline.transfer.datastoring.DataStoringInterface;
import de.ume.deidentifhirpipeline.transfer.lastupdated.GetLastUpdated;
import de.ume.deidentifhirpipeline.transfer.lastupdated.SetLastUpdated;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.Pseudonymization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@Component
public class TransferProcess {

  private final Map<String, CohortSelectionInterface> cohortSelectionImplementations;
  private final Map<String, GetLastUpdated>           getLastUpdatedImplementations;
  private final Map<String, DataSelectionInterface>   dataSelectionImplementations;
  private final Map<String, Pseudonymization>         pseudonymizationImplementations;
  private final Map<String, DataStoringInterface>     dataStoringImplementations;
  private final Map<String, SetLastUpdated>           setLastUpdatedImplementations;

  public TransferProcess(
      Map<String, CohortSelectionInterface> cohortSelectionImplementations,
      Map<String, GetLastUpdated> getLastUpdatedImplementations,
      Map<String, DataSelectionInterface> dataSelectionImplementations,
      Map<String, Pseudonymization> pseudonymizationImplementations,
      Map<String, DataStoringInterface> dataStoringImplementations,
      Map<String, SetLastUpdated> setLastUpdatedImplementations) {
    this.cohortSelectionImplementations  = cohortSelectionImplementations;
    this.getLastUpdatedImplementations   = getLastUpdatedImplementations;
    this.dataSelectionImplementations    = dataSelectionImplementations;
    this.pseudonymizationImplementations = pseudonymizationImplementations;
    this.dataStoringImplementations      = dataStoringImplementations;
    this.setLastUpdatedImplementations   = setLastUpdatedImplementations;
    cohortSelectionImplementations.keySet().forEach(s -> log.info("Cohort Selection: " + s));
    dataSelectionImplementations.keySet().forEach(s -> log.info("Data Selection: " + s));
    getLastUpdatedImplementations.keySet().forEach(s -> log.info("GetLastUpdated: " + s));
  }

  public CohortSelectionInterface get(String implementation) {
    return cohortSelectionImplementations.get(implementation);
  }

  public void execute(ProjectConfig projectConfig) throws Exception {
    CohortSelectionInterface cohortselection;
    if (projectConfig.getCohortSelection().getViaIds() != null) {
      cohortselection = get("cohortselectionimpl");
    } else if (projectConfig.getCohortSelection().getViaFile() != null) {
      cohortselection = get("cohortselectionimpl2");
    } else {
      cohortselection = null;
    }
    cohortselection.before(projectConfig);

  }

  public String startNew(ProjectConfig projectConfig) throws Exception {
    UUID uuid = UUID.randomUUID();

    beforeExecution(projectConfig);

    CohortSelectionInterface cohortSelection = this.getCohortSelection(projectConfig);

    List<String> ids = cohortSelection.before(projectConfig);
    CompletableFuture.supplyAsync(() -> processNew(uuid, ids, projectConfig));

    return uuid.toString();
  }

  // public static String start(List<String> ids, ProjectConfig projectConfig) throws Exception {
  // UUID uuid = UUID.randomUUID();
  //
  // beforeExecution(projectConfig);
  // CompletableFuture.supplyAsync(() -> process(uuid, ids, projectConfig));
  //
  // return uuid.toString();
  // }
  //
  // public static String start(ProjectConfig projectConfig) throws Exception {
  // UUID uuid = UUID.randomUUID();
  //
  // beforeExecution(projectConfig);
  //
  // List<String> ids = CohortSelection.beforeExecution(projectConfig);
  // CompletableFuture.supplyAsync(() -> process(uuid, ids, projectConfig));
  //
  // return uuid.toString();
  // }

  private void beforeExecution(ProjectConfig projectConfig) throws Exception {
    GetLastUpdated getLastUpdated = this.getLastUpdated(projectConfig);
    DataSelectionInterface dataSelection = this.getDataSelection(projectConfig);
    Pseudonymization pseudonymization = this.getPseudonymization(projectConfig);
    DataStoringInterface dataStoring = this.getDataStoring(projectConfig);

    if (getLastUpdated != null)
      getLastUpdated.beforeExecution(projectConfig);
    dataSelection.beforeExecution(projectConfig);
    pseudonymization.beforeExecution(projectConfig);
    dataStoring.beforeExecution(projectConfig);
  }

  private String processNew(UUID uuid, List<String> ids, ProjectConfig projectConfig) {
    log.debug("Starting transfer: " + uuid.toString());

    Transfer transfer = new Transfer(uuid);
    List<Context> contexts = setUpContexts(transfer, ids, projectConfig);

    log.info("Number of bundles: {}", contexts.size());

    GetLastUpdated getLastUpdated = this.getLastUpdated(projectConfig);
    DataSelectionInterface dataSelection = getDataSelection(projectConfig);
    Pseudonymization pseudonymization = getPseudonymization(projectConfig);
    DataStoringInterface dataStoring = this.getDataStoring(projectConfig);
    SetLastUpdated setLastUpdated = this.getSetLastUpdated(projectConfig);

    ForkJoinPool pool = new ForkJoinPool(projectConfig.getParallelism());
    pool.submit(() -> {
      contexts.stream().parallel()
          .map(context -> {
            if (getLastUpdated != null)
              return getLastUpdated.execute(context);
            else
              return context;
          }).filter(context -> !context.isFailed())
          .map(dataSelection::execute).filter(context -> !context.isFailed())
          .map(pseudonymization::execute).filter(context -> !context.isFailed())
          .map(dataStoring::execute).filter(context -> !context.isFailed())
          .map(context -> {
            if (setLastUpdated != null)
              return setLastUpdated.execute(context);
            else
              return context;
          }).filter(context -> !context.isFailed())
          .forEach(context -> {
            context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.completed());
            log.info(String.format("Transfer for patient id: '%s' finished successfully.",
                context.getPatientId()));
          });
      transfer.setFinalStatus();
    });

    return uuid.toString();
  }

  // private static String process(UUID uuid, List<String> ids, ProjectConfig projectConfig) {
  // log.debug("Starting transfer: " + uuid.toString());
  //
  // Transfer transfer = new Transfer(uuid);
  // List<Context> contexts = setUpContexts(transfer, ids, projectConfig);
  //
  // log.info("Number of bundles: {}", contexts.size());
  // log.info("Parallelism is set to: {}", projectConfig.getParallelism());
  //
  // ForkJoinPool pool = new ForkJoinPool(projectConfig.getParallelism());
  // pool.submit(() -> {
  // contexts.stream().parallel()
  // .map(GetLastUpdated::execute).filter(context -> !context.isFailed())
  // .map(DataSelection::execute).filter(context -> !context.isFailed())
  // .map(Pseudonymization::execute).filter(context -> !context.isFailed())
  // .map(DataStoring::execute).filter(context -> !context.isFailed())
  // .map(SetLastUpdated::execute).filter(context -> !context.isFailed())
  // .forEach(context -> {
  // context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.completed());
  // log.info(String.format("Transfer for patient id: '%s' finished successfully.",
  // context.getPatientId()));
  // });
  // transfer.setFinalStatus();
  // });
  //
  // return uuid.toString();
  // }

  private static List<Context> setUpContexts(Transfer transfer, List<String> ids, ProjectConfig projectConfig) {
    List<Context> contexts = new ArrayList<>();
    for (String id : ids) {
      transfer.getMap().put(id, new TransferStatus());
      Context context = new Context(transfer, projectConfig, id);
      contexts.add(context);
    }
    return contexts;
  }

  private GetLastUpdated getLastUpdated(ProjectConfig projectConfig) {
    GetLastUpdated getLastUpdated;
    if (projectConfig.getLastUpdated() != null) {
      getLastUpdated = getLastUpdatedImplementations.get("get-last-updated");
    } else {
      getLastUpdated = null;
    }
    return getLastUpdated;
  }

  private SetLastUpdated getSetLastUpdated(ProjectConfig projectConfig) {
    SetLastUpdated setLastUpdated;
    if (projectConfig.getLastUpdated() != null) {
      setLastUpdated = setLastUpdatedImplementations.get("set-last-updated");
    } else {
      setLastUpdated = null;
    }
    return setLastUpdated;
  }

  private CohortSelectionInterface getCohortSelection(ProjectConfig projectConfig) {
    CohortSelectionInterface cohortselection;
    if (projectConfig.getCohortSelection().getViaIds() != null) {
      cohortselection = get("cohort-selection.via-ids");
    } else if (projectConfig.getCohortSelection().getViaFile() != null) {
      cohortselection = get("cohort-selection.via-file");
    } else if (projectConfig.getCohortSelection().getGics() != null) {
      cohortselection = get("cohort-selection.gics");
    } else if (projectConfig.getCohortSelection().getFiremetrics() != null) {
      cohortselection = get("cohort-selection.firemetrics");
    } else {
      cohortselection = null;
    }
    return cohortselection;
  }

  private DataSelectionInterface getDataSelection(ProjectConfig projectConfig) {
    DataSelectionInterface dataSelection;
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

  private DataStoringInterface getDataStoring(ProjectConfig projectConfig) {
    DataStoringInterface dataStoring;
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

  private Pseudonymization getPseudonymization(ProjectConfig projectConfig) {
    Pseudonymization pseudonymization;
    if (projectConfig.getPseudonymization().getDeidentifhir() != null) {
      pseudonymization = pseudonymizationImplementations.get("pseudonymization.deidentifhir");
    } else {
      pseudonymization = pseudonymizationImplementations.get("pseudonymization.none");
    }
    return pseudonymization;
  }


}
