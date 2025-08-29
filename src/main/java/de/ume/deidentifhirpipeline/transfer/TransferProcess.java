package de.ume.deidentifhirpipeline.transfer;

import de.ume.deidentifhirpipeline.api.data.Status;
import de.ume.deidentifhirpipeline.api.data.Transfer;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.api.data.Transfers;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.cohortselection.CohortSelection;
import de.ume.deidentifhirpipeline.transfer.dataselection.DataSelection;
import de.ume.deidentifhirpipeline.transfer.datastoring.DataStoring;
import de.ume.deidentifhirpipeline.transfer.lastupdated.GetLastUpdated;
import de.ume.deidentifhirpipeline.transfer.lastupdated.SetLastUpdated;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.Pseudonymization;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
public class TransferProcess {

  private final ImplementationsFactory implementationsFactory;

  public TransferProcess(ImplementationsFactory implementationsFactory) {
    this.implementationsFactory = implementationsFactory;
  }

  public String startNew(ProjectConfig projectConfig) throws Exception {
    UUID uuid = UUID.randomUUID();

    beforeExecution(projectConfig);

    CohortSelection cohortSelection = implementationsFactory.getCohortSelection(projectConfig);

    Cohort cohort = cohortSelection.before(projectConfig);
    if (projectConfig.isUseVirtualThreads()) {
      log.info("Virtual threads are used for processing");
      CompletableFuture.supplyAsync(() -> processWithVirtualThreads(uuid, cohort, projectConfig));
    } else {
      CompletableFuture.supplyAsync(() -> processWithParallelStream(uuid, cohort, projectConfig));
    }

    return uuid.toString();
  }

  public String retry(ProjectConfig projectConfig, Transfer transfer) throws Exception {
    UUID uuid = UUID.randomUUID();

    beforeExecution(projectConfig);

    Cohort cohort = new Cohort(transfer.getMap().entrySet().stream()
        .filter(entry -> entry.getValue().getStatus().equals(Status.FAILED))
        .map(entry -> entry.getKey())
        .toList(),
        transfer.getFilteredOutCohortIdsWithErrorMessages());
    if (projectConfig.isUseVirtualThreads()) {
      log.info("Virtual threads are used for processing");
      CompletableFuture.supplyAsync(() -> processWithVirtualThreads(uuid, cohort, projectConfig, transfer));
    } else {
      CompletableFuture.supplyAsync(() -> processWithParallelStream(uuid, cohort, projectConfig, transfer));
    }

    return uuid.toString();
  }

  private void beforeExecution(ProjectConfig projectConfig) throws Exception {

    if (projectConfig.getGetLastUpdatedImpl().isPresent())
      projectConfig.getGetLastUpdatedImpl().get().beforeExecution(projectConfig);
    projectConfig.getDataSelectionImpl().before(projectConfig);
    projectConfig.getPseudonymizationImpl().before(projectConfig);
    projectConfig.getDataStoringImpl().before(projectConfig);
    if (projectConfig.getSetLastUpdatedImpl().isPresent())
      projectConfig.getSetLastUpdatedImpl().get().beforeExecution(projectConfig);
  }

  private String processWithParallelStream(UUID uuid, Cohort cohort, ProjectConfig projectConfig) {
    return processWithParallelStream(uuid, cohort, projectConfig, null);
  }

  private String processWithParallelStream(UUID uuid, Cohort cohort, ProjectConfig projectConfig, Transfer transfer) {
    log.debug("Starting transfer: " + uuid.toString());

    final Transfer transferToBeExecuted;
    if (transfer == null)
      transferToBeExecuted = new Transfer(projectConfig.getName(), cohort.filteredOutIdsWithErrorMessages());
    else
      transferToBeExecuted = transfer;

    List<Context> contexts = setUpContexts(uuid, transferToBeExecuted, cohort.ids(), projectConfig);

    log.info("Number of bundles: {}", contexts.size());

    Optional<GetLastUpdated> getLastUpdated = projectConfig.getGetLastUpdatedImpl();
    DataSelection dataSelection = projectConfig.getDataSelectionImpl();
    Pseudonymization pseudonymization = projectConfig.getPseudonymizationImpl();
    DataStoring dataStoring = projectConfig.getDataStoringImpl();
    Optional<SetLastUpdated> setLastUpdated = projectConfig.getSetLastUpdatedImpl();

    log.info("Parallelism is set to: {}", projectConfig.getParallelism());

    ForkJoinPool pool = new ForkJoinPool(projectConfig.getParallelism());
    pool.submit(() -> {
      contexts.stream().parallel()
          .map(context -> executeGetLastUpdated(getLastUpdated, context)).filter(context -> !context.isFailed())
          .map(context -> executeDataSelection(dataSelection, context)).filter(context -> !context.isFailed())
          .map(context -> executePseudonymization(pseudonymization, context)).filter(context -> !context.isFailed())
          .map(context -> executeDataStoring(dataStoring, context)).filter(context -> !context.isFailed())
          .map(context -> executeSetLastUpdated(setLastUpdated, context)).filter(context -> !context.isFailed())
          .forEach(context -> {
            context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.completed());
            log.info(String.format("Transfer for patient id: '%s' finished successfully.",
                context.getPatientId()));
          });
      transferToBeExecuted.setFinalStatus();
    });

    return uuid.toString();
  }

  private String processWithVirtualThreads(UUID uuid, Cohort cohort, ProjectConfig projectConfig) {
    return processWithVirtualThreads(uuid, cohort, projectConfig, null);
  }

  private String processWithVirtualThreads(UUID uuid, Cohort cohort, ProjectConfig projectConfig, Transfer transfer) {
    log.debug("Starting transfer: " + uuid.toString());

    if (transfer == null)
      transfer = new Transfer(projectConfig.getName(), cohort.filteredOutIdsWithErrorMessages());

    List<Context> contexts = setUpContexts(uuid, transfer, cohort.ids(), projectConfig);

    log.info("Number of bundles: {}", contexts.size());

    Optional<GetLastUpdated> getLastUpdated = projectConfig.getGetLastUpdatedImpl();
    DataSelection dataSelection = projectConfig.getDataSelectionImpl();
    Pseudonymization pseudonymization = projectConfig.getPseudonymizationImpl();
    DataStoring dataStoring = projectConfig.getDataStoringImpl();
    Optional<SetLastUpdated> setLastUpdated = projectConfig.getSetLastUpdatedImpl();

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      for (Context context : contexts) {
        Future future = executorService.submit(() -> {
          executeGetLastUpdated(getLastUpdated, context);
          executeDataSelectionWithSemaphore(dataSelection, context);
          executePseudonymizationWithSemaphore(pseudonymization, context);
          executeDataStoringWithSemaphore(dataStoring, context);
          executeSetLastUpdated(setLastUpdated, context);
          if (!context.isFailed()) {
            context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.completed());
            log.info(String.format("Transfer for patient id: '%s' finished successfully.", context.getPatientId()));
          }
        });
        context.setFuture(future);
      }
    }
    contexts.stream().forEach(context -> {
      try {
        context.getFuture().get();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (ExecutionException e) {
        throw new RuntimeException(e);
      }
    });
    transfer.setFinalStatus();

    return uuid.toString();
  }

  private static Context executeGetLastUpdated(Optional<GetLastUpdated> getLastUpdated, Context context) {
    if (!context.isFailed() && getLastUpdated.isPresent()) {
      try {
        context = getLastUpdated.get().process(context);
      } catch (Exception e) {
        Utils.handleException(context, e);
      }
    }
    return context;
  }

  private static Context executeSetLastUpdated(Optional<SetLastUpdated> setLastUpdated, Context context) {
    if (!context.isFailed() && setLastUpdated.isPresent()) {
      try {
        context = setLastUpdated.get().process(context);
      } catch (Exception e) {
        Utils.handleException(context, e);
      }
    }
    return context;
  }

  private static void executeDataSelectionWithSemaphore(DataSelection dataSelection, Context context) {
    if (!context.isFailed()) {
      try {
        context.getProjectConfig().getDataSelection().getSemaphore().acquire();
        executeDataSelection(dataSelection, context);
      } catch (InterruptedException e) {
        log.info("InterruptedException: " + e.getMessage());
        e.printStackTrace();
        Thread.currentThread().interrupt();
      } finally {
        context.getProjectConfig().getDataSelection().getSemaphore().release();
      }
    }
  }

  private static Context executeDataSelection(DataSelection dataSelection, Context context) {
    if (!context.isFailed()) {
      try {
        Bundle bundle = dataSelection.process(context);
        context.setBundle(bundle);
      } catch (Exception e) {
        Utils.handleException(context, e);
      }
    }
    return context;
  }

  private static void executePseudonymizationWithSemaphore(Pseudonymization pseudonymization, Context context) {
    if (!context.isFailed()) {
      try {
        context.getProjectConfig().getPseudonymization().getSemaphore().acquire();
        executePseudonymization(pseudonymization, context);
      } catch (InterruptedException e) {
        log.info("InterruptedException: " + e.getMessage());
        e.printStackTrace();
        Thread.currentThread().interrupt();
      } finally {
        context.getProjectConfig().getPseudonymization().getSemaphore().release();
      }
    }
  }

  private static Context executePseudonymization(Pseudonymization pseudonymization, Context context) {
    if (!context.isFailed()) {
      try {
        Bundle bundle = pseudonymization.process(context);
        context.setBundle(bundle);
      } catch (Exception e) {
        Utils.handleException(context, e);
      }
    }
    return context;
  }

  private static void executeDataStoringWithSemaphore(DataStoring dataStoring, Context context) {
    if (!context.isFailed()) {
      try {
        context.getProjectConfig().getDataStoring().getSemaphore().acquire();
        executeDataStoring(dataStoring, context);
      } catch (InterruptedException e) {
        log.info("InterruptedException: " + e.getMessage());
        e.printStackTrace();
        Thread.currentThread().interrupt();
      } finally {
        context.getProjectConfig().getDataStoring().getSemaphore().release();
      }
    }
  }

  private static Context executeDataStoring(DataStoring dataStoring, Context context) {
    if (!context.isFailed()) {
      try {
        dataStoring.process(context);
      } catch (Exception e) {
        Utils.handleException(context, e);
      }
    }
    return context;
  }

  private static List<Context> setUpContexts(UUID uuid, Transfer transfer, List<String> ids, ProjectConfig projectConfig) {
    Transfers.getMap().put(uuid.toString(), transfer);
    List<Context> contexts = new ArrayList<>();
    for (String id : ids) {
      transfer.getMap().put(id, new TransferStatus());
      Context context = new Context(transfer, projectConfig, id);
      contexts.add(context);
    }
    return contexts;
  }

}
