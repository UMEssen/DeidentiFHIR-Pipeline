package de.ume.deidentifhirpipeline.api.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transfer {
  private final ConcurrentMap<String, TransferStatus> map = new ConcurrentHashMap<>();

  private final LocalDateTime   startDateTime;
  @Setter private LocalDateTime endDateTime;
  @Setter private LocalDateTime statusDateTime;
  @Setter private Status        status;

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
    if (map.values().stream().allMatch(transferStatus -> transferStatus.getStatus() == Status.COMPLETED)) {
      status = Status.COMPLETED;
    } else if (map.values().stream().allMatch(transferStatus -> transferStatus.getStatus() == Status.FAILED)) {
      status = Status.FAILED;
    } else {
      status = Status.PARTIALLY_FAILED;
    }
  }
}
