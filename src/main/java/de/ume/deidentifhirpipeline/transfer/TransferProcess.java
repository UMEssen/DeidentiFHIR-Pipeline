package de.ume.deidentifhirpipeline.transfer;

import de.ume.deidentifhirpipeline.api.data.Transfer;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
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

    List<String> ids = cohortSelection.before(projectConfig);
    CompletableFuture.supplyAsync(() -> processWithVirtualThreads(uuid, ids, projectConfig));

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

  private String processWithVirtualThreads(UUID uuid, List<String> ids, ProjectConfig projectConfig) {
    log.debug("Starting transfer: " + uuid.toString());

    Transfer transfer = new Transfer(uuid);
    List<Context> contexts = setUpContexts(transfer, ids, projectConfig);

    log.info("Number of bundles: {}", contexts.size());

    Optional<GetLastUpdated> getLastUpdated = projectConfig.getGetLastUpdatedImpl();
    DataSelection dataSelection = projectConfig.getDataSelectionImpl();
    Pseudonymization pseudonymization = projectConfig.getPseudonymizationImpl();
    DataStoring dataStoring = projectConfig.getDataStoringImpl();
    Optional<SetLastUpdated> setLastUpdated = projectConfig.getSetLastUpdatedImpl();

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      for (Context context : contexts) {
        Future future = executorService.submit(() -> {
          getLastUpdated.ifPresent(x -> x.execute(context));
          executeDataSelection(dataSelection, context);
          executePseudonymization(pseudonymization, context);
          executeDataStoring(dataStoring, context);
          setLastUpdated.ifPresent(x -> x.execute(context));
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

  private static void executeDataSelection(DataSelection dataSelection, Context context) {
    if (!context.isFailed()) {
      try {
        context.getProjectConfig().getDataSelection().getSemaphore().acquire();
        Bundle bundle = dataSelection.process(context);
        context.setBundle(bundle);
      } catch (InterruptedException e) {
        log.info("InterruptedException: " + e.getMessage());
        e.printStackTrace();
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        Utils.handleException(context, e);
      } finally {
        context.getProjectConfig().getDataSelection().getSemaphore().release();
      }
    }
  }

  private static void executePseudonymization(Pseudonymization pseudonymization, Context context) {
    if (!context.isFailed()) {
      try {
        context.getProjectConfig().getPseudonymization().getSemaphore().acquire();
        Bundle bundle = pseudonymization.process(context);
        context.setBundle(bundle);
      } catch (InterruptedException e) {
        log.info("InterruptedException: " + e.getMessage());
        e.printStackTrace();
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        Utils.handleException(context, e);
      } finally {
        context.getProjectConfig().getPseudonymization().getSemaphore().release();
      }
    }
  }

  private static void executeDataStoring(DataStoring dataStoring, Context context) {
    if (!context.isFailed()) {
      try {
        context.getProjectConfig().getDataStoring().getSemaphore().acquire();
        dataStoring.process(context);
      } catch (InterruptedException e) {
        log.info("InterruptedException: " + e.getMessage());
        e.printStackTrace();
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        Utils.handleException(context, e);
      } finally {
        context.getProjectConfig().getDataStoring().getSemaphore().release();
      }
    }
  }

  private static List<Context> setUpContexts(Transfer transfer, List<String> ids, ProjectConfig projectConfig) {
    List<Context> contexts = new ArrayList<>();
    for (String id : ids) {
      transfer.getMap().put(id, new TransferStatus());
      Context context = new Context(transfer, projectConfig, id);
      contexts.add(context);
    }
    return contexts;
  }

}
