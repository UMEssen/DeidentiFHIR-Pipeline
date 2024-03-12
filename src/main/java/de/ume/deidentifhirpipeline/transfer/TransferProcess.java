package de.ume.deidentifhirpipeline.transfer;

import de.ume.deidentifhirpipeline.api.data.Transfer;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.transfer.cohortselection.CohortSelection;
import de.ume.deidentifhirpipeline.transfer.dataselection.DataSelection;
import de.ume.deidentifhirpipeline.transfer.datastoring.DataStoring;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.Pseudonymization;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class TransferProcess {

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
      contexts.stream().parallel().map(DataSelection::execute)
          .filter(context -> !context.isFailed()).map(Pseudonymization::execute)
          .filter(context -> !context.isFailed()).map(DataStoring::execute)
          .filter(context -> !context.isFailed()).forEach(context -> {
            context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.completed());
            log.info(String.format("Transfer for patient id: '%s' finished successfully.",
                context.getPatientId()));
          });
      transfer.setFinalStatus();
    });
    log.info("Transfer finished");

//    ParallelFlowable<Context> source = Flowable.fromIterable(contexts).parallel(8);
//    Flowable<Context> result = source
//        .runOn(Schedulers.io())
//        .map(DataSelection::execute)
//        .filter(context -> !context.isFailed())
//        .map(Pseudonymization::execute)
//        .filter(context -> !context.isFailed())
//        .map(DataStoring::execute)
//        .filter(context -> !context.isFailed())
////        .doOnNext(context -> {
////          context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.completed());
////        })
//        .sequential();
////        .throttleWithTimeout(projectConfiguration.getDataSelection().getFiremetrics().getMillisPerItem(), TimeUnit.MILLISECONDS);
//    result.blockingForEach(context -> {
////      Transfers.setFinalTransferStatus(uuid);
//      log.info(String.format("Transfer for patient id: '%s' finished successfully.", context.getPatientId()));
//    });
//    log.info(String.format("Number of transfered bundles for transfer id '%s': %d", uuid.toString(), result.toList().blockingGet().size()));

    return uuid.toString();
  }

  private static List<Context> setUpContexts(Transfer transfer, List<String> ids,
      ProjectConfiguration projectConfiguration) {
    List<Context> contexts = new ArrayList<>();
    for( String id : ids ) {
      transfer.getMap().put(id, new TransferStatus());
      Context context = new Context(transfer, projectConfiguration, id,null, false, null);
      contexts.add(context);
    }
    return contexts;
  }


}
