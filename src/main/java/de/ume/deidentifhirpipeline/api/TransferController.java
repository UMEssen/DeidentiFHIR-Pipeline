package de.ume.deidentifhirpipeline.api;

import de.ume.deidentifhirpipeline.api.data.Status;
import de.ume.deidentifhirpipeline.api.data.Transfer;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.api.data.Transfers;
import de.ume.deidentifhirpipeline.api.model.TransferRequest;
import de.ume.deidentifhirpipeline.api.model.TransferRequestWithConfig;
import de.ume.deidentifhirpipeline.api.model.TransferResponse;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.ProjectsConfig;
import de.ume.deidentifhirpipeline.service.pseudonymization.HashmapService;
import de.ume.deidentifhirpipeline.transfer.ImplementationsFactory;
import de.ume.deidentifhirpipeline.transfer.TransferProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


@RestController
public class TransferController {

  @Autowired ProjectsConfig         projectsConfig;
  @Autowired TransferProcess        transferProcess;
  @Autowired ImplementationsFactory implementationsFactory;

  // @PostMapping(value = "/start", consumes = "application/json", produces = "application/json")
  // public ResponseEntity<TransferResponse> start(@RequestBody TransferRequest transferRequest)
  // throws Exception {
  //
  // ProjectConfig projectConfig = projectsConfig.getProjects().get(transferRequest.getProject());
  //
  // if (projectConfig == null) {
  // return new ResponseEntity<>(new TransferResponse(String.format("Project '%s' not configured",
  // transferRequest.getProject())), HttpStatus.NOT_FOUND);
  // } else {
  // String response = TransferProcess.start(projectConfig);
  // return new ResponseEntity<>(new TransferResponse(response), HttpStatusCode.valueOf(200));
  // }
  // }

  @PostMapping(value = "/start", consumes = "application/json", produces = "application/json")
  public ResponseEntity<TransferResponse> startTest(@RequestBody TransferRequest transferRequest) throws Exception {

    ProjectConfig projectConfig = projectsConfig.getProjects().get(transferRequest.getProject());
    projectConfig.setup(transferRequest.getProject(), implementationsFactory);

    if (projectConfig == null) {
      return new ResponseEntity<>(new TransferResponse(String.format("Project '%s' not configured", transferRequest.getProject())), HttpStatus.NOT_FOUND);
    } else {
      String response = transferProcess.startNew(projectConfig);
      return new ResponseEntity<>(new TransferResponse(response), HttpStatusCode.valueOf(200));
    }
  }

  @PostMapping(value = "/start-with-changed-configuration", consumes = "application/json", produces = "application/json")
  public ResponseEntity<TransferResponse> startWithChangedConfiguration(@RequestBody TransferRequestWithConfig transferRequestWithConfig) throws Exception {

    ProjectConfig projectConfig = projectsConfig.getProjects().get(transferRequestWithConfig.getProject());

    if (projectConfig == null) {
      return new ResponseEntity<>(new TransferResponse(String.format("Project '%s' not configured", transferRequestWithConfig.getProject())),
          HttpStatus.NOT_FOUND);
    } else {
      projectConfig = projectConfig.apply(transferRequestWithConfig.getProjectConfig());
      projectConfig.setup(transferRequestWithConfig.getProject(), implementationsFactory);

      String response = transferProcess.startNew(projectConfig);
      return new ResponseEntity<>(new TransferResponse(response), HttpStatusCode.valueOf(200));
    }
  }

  @PostMapping(value = "/start-with-new-configuration", consumes = "application/json", produces = "application/json")
  public ResponseEntity<TransferResponse> startWithNewConfiguration(@RequestBody TransferRequestWithConfig transferRequestWithConfig) throws Exception {
    ProjectConfig projectConfig = transferRequestWithConfig.getProjectConfig();
    projectConfig.setup(transferRequestWithConfig.getProject(), implementationsFactory);

    String response = transferProcess.startNew(projectConfig);
    return new ResponseEntity<>(new TransferResponse(response), HttpStatusCode.valueOf(200));
  }

  @GetMapping(value = "/transfer")
  public ResponseEntity<Set<String>> getTransfers() {
    return new ResponseEntity<>(Transfers.getMap().keySet(), HttpStatusCode.valueOf(200));
  }

  @GetMapping(value = "/transfer/all")
  public ResponseEntity<Map<String, Transfer>> getTransfersAll() {
    Transfers.getMap().values().forEach(transfer -> transfer.updateStatus());
    return new ResponseEntity<>(Transfers.getMap(), HttpStatusCode.valueOf(200));
  }

  @GetMapping(value = "/transfer/{id}")
  public ResponseEntity<Transfer> getTransfer(@PathVariable("id") String id) {
    Transfers.getMap().get(id).updateStatus();
    return new ResponseEntity<>(Transfers.getMap().get(id), HttpStatusCode.valueOf(200));
  }

  @GetMapping(value = "/transfer/{id}/failed")
  public ResponseEntity<Map<String, TransferStatus>> getFailedTransfer(@PathVariable("id") String id) {
    Map<String, TransferStatus> transfers = Transfers.getMap().get(id).getMap();
    Map<String, TransferStatus> bla = transfers.entrySet().stream()
        .filter(entry -> entry.getValue().getStatus().equals(Status.FAILED))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    return new ResponseEntity<>(bla, HttpStatusCode.valueOf(200));
  }

  @GetMapping(value = "/transfer/{id}/pending")
  public ResponseEntity<Map<String, TransferStatus>> getPendingTransfer(@PathVariable("id") String id) {
    Map<String, TransferStatus> transfers = Transfers.getMap().get(id).getMap();
    Map<String, TransferStatus> bla = transfers.entrySet().stream()
        .filter(entry -> entry.getValue().getStatus().equals(Status.PENDING))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    return new ResponseEntity<>(bla, HttpStatusCode.valueOf(200));
  }

  @GetMapping(value = "/transfer/{id}/completed")
  public ResponseEntity<Map<String, TransferStatus>> getCompletedTransfer(@PathVariable("id") String id) {
    Map<String, TransferStatus> transfers = Transfers.getMap().get(id).getMap();
    Map<String, TransferStatus> bla = transfers.entrySet().stream()
        .filter(entry -> entry.getValue().getStatus().equals(Status.COMPLETED))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    return new ResponseEntity<>(bla, HttpStatusCode.valueOf(200));
  }

  @GetMapping(value = "/hashmap")
  public ResponseEntity<ConcurrentMap<String, ConcurrentMap<String, String>>> getPseudonymMap() {
    return new ResponseEntity<>(HashmapService.domainMap, HttpStatusCode.valueOf(200));
  }

  @GetMapping(value = "/healthcheck")
  public ResponseEntity<String> healthcheck() {
    return new ResponseEntity<>("OK", HttpStatusCode.valueOf(200));
  }

}
