package de.ume.deidentifhirpipeline.api.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transfer {

  private final LocalDateTime   startDateTime;
  @Setter private LocalDateTime endDateTime;
  @Setter private LocalDateTime statusDateTime;
  @Setter private Status        status;
  @Setter private int           total;
  @Setter private int           pending;
  @Setter private int           completed;
  @Setter private int           failed;

  private Map<String, Integer> errorMessagesWithCount;

  private final ConcurrentMap<String, TransferStatus> map = new ConcurrentHashMap<>();

  public Transfer(UUID uuid) {
    this.startDateTime = LocalDateTime.now();
    this.status        = Status.PENDING;
    Transfers.getMap().put(uuid.toString(), this);
  }

  public String toString() {
    return startDateTime + ", " + map.toString();
  }

  public void setFinalStatus() {
    endDateTime = LocalDateTime.now();

    this.updateStatus();

    if (map.values().stream().allMatch(transferStatus -> transferStatus.getStatus() == Status.COMPLETED)) {
      status = Status.COMPLETED;
    } else if (map.values().stream().allMatch(transferStatus -> transferStatus.getStatus() == Status.FAILED)) {
      status = Status.FAILED;
    } else {
      status = Status.PARTIALLY_FAILED;
    }
  }

  public void updateStatus() {
    // Update only, when processing is not finished. Final status is set after processing.
    // if (status == Status.PENDING) {
    total     = map.values().size();
    pending   = map.values().stream()
        .filter(transferStatus -> transferStatus.getStatus() == Status.PENDING)
        .toList()
        .size();
    completed = map.values().stream()
        .filter(transferStatus -> transferStatus.getStatus() == Status.COMPLETED)
        .toList()
        .size();
    failed    = map.values().stream()
        .filter(transferStatus -> transferStatus.getStatus() == Status.FAILED)
        .toList()
        .size();

    errorMessagesWithCount = new HashMap<>();
    map.values().stream()
        .filter(transferStatus -> transferStatus.getException() != null) // && transferStatus.getException().getMessage() != null)
        .forEach(transferStatus -> {
          String message = transferStatus.getException().getClass().getSimpleName() + " - " + transferStatus.getException().getMessage();
          errorMessagesWithCount.computeIfPresent(message, (key, value) -> value + 1);
          errorMessagesWithCount.computeIfAbsent(message, key -> 1);
        });
    if (errorMessagesWithCount.isEmpty())
      errorMessagesWithCount = null;
    // }
  }
}
