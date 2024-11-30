package de.ume.deidentifhirpipeline.api.data;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Transfers {
  @Getter private static final ConcurrentMap<String, Transfer> map = new ConcurrentHashMap<>();

  private Transfers() {
    throw new IllegalStateException("Utility class");
  }

  public static void setFinalTransferStatus(UUID uuid) {
    Transfer transfer = Transfers.map.get(uuid.toString());
    transfer.setStatusDateTime(LocalDateTime.now());
    if (transfer.getMap().values().stream().allMatch(t -> t.getStatus() == Status.COMPLETED))
      transfer.setStatus(Status.COMPLETED);
    else if (transfer.getMap().values().stream().allMatch(t -> t.getStatus() == Status.FAILED))
      transfer.setStatus(Status.FAILED);
    else if (transfer.getMap().values().stream().anyMatch(t -> t.getStatus() == Status.PENDING))
      transfer.setStatus(Status.PENDING);
    else
      transfer.setStatus(Status.PARTIALLY_FAILED);
  }
}
