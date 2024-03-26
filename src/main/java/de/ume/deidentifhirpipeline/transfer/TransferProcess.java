package de.ume.deidentifhirpipeline.transfer;

import de.ume.deidentifhirpipeline.api.data.Transfer;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.transfer.cohortselection.CohortSelection;
import de.ume.deidentifhirpipeline.transfer.dataselection.DataSelection;
import de.ume.deidentifhirpipeline.transfer.datastoring.DataStoring;
import de.ume.deidentifhirpipeline.transfer.lastupdated.GetLastUpdated;
import de.ume.deidentifhirpipeline.transfer.lastupdated.SetLastUpdated;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.Pseudonymization;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class TransferProcess {

  private TransferProcess() {
    throw new IllegalStateException("Utility class");
  }

  public static String start(List<String> ids,
      ProjectConfiguration projectConfiguration) throws Exception {
    UUID uuid = UUID.randomUUID();

    beforeExecution(projectConfiguration);
    CompletableFuture.supplyAsync(() -> process(uuid, ids, projectConfiguration));

    return uuid.toString();
  }

  public static String start(ProjectConfiguration projectConfiguration) throws Exception {
    UUID uuid = UUID.randomUUID();

    beforeExecution(projectConfiguration);

    List<String> ids = CohortSelection.beforeExecution(projectConfiguration);
    CompletableFuture.supplyAsync(() -> process(uuid, ids, projectConfiguration));

    return uuid.toString();
  }

  private static void beforeExecution(ProjectConfiguration projectConfiguration) throws Exception {
    GetLastUpdated.beforeExecution(projectConfiguration);
    DataSelection.beforeExecution(projectConfiguration);
    Pseudonymization.beforeExecution(projectConfiguration);
    DataStoring.beforeExecution(projectConfiguration);
  }

  private static String process(UUID uuid, List<String> ids, ProjectConfiguration projectConfiguration) {
    log.debug("Starting transfer: " + uuid.toString());

    Transfer transfer = new Transfer(uuid);
    List<Context> contexts = setUpContexts(transfer, ids, projectConfiguration);

    log.info("Number of bundles: {}", contexts.size());
    log.info("Parallelism is set to: {}", projectConfiguration.getParallelism());

    ForkJoinPool pool = new ForkJoinPool(projectConfiguration.getParallelism());
    pool.submit(() -> {
      contexts.stream().parallel()
          .map(GetLastUpdated::execute).filter(context -> !context.isFailed())
          .map(DataSelection::execute).filter(context -> !context.isFailed())
          .map(Pseudonymization::execute).filter(context -> !context.isFailed())
          .map(DataStoring::execute).filter(context -> !context.isFailed())
          .map(SetLastUpdated::execute).filter(context -> !context.isFailed())
          .forEach(context -> {
            context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.completed());
            log.info(String.format("Transfer for patient id: '%s' finished successfully.",
                context.getPatientId()));
          });
      transfer.setFinalStatus();
    });

    return uuid.toString();
  }

  private static List<Context> setUpContexts(Transfer transfer, List<String> ids,
      ProjectConfiguration projectConfiguration) {
    List<Context> contexts = new ArrayList<>();
    for( String id : ids ) {
      transfer.getMap().put(id, new TransferStatus());
      Context context = new Context(transfer, projectConfiguration, id);
      contexts.add(context);
    }
    return contexts;
  }


}
