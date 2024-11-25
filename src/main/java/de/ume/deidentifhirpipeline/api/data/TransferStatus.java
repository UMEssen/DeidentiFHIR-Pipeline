package de.ume.deidentifhirpipeline.api.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferStatus {
  private Status status;
  private LocalDateTime endDate;

  private Exception exception;

  public TransferStatus() {
    status = Status.PENDING;
  }

  public TransferStatus(Status status) {
    this.status = status;
  }

  public TransferStatus(Status status, LocalDateTime endDate) {
    this.status = status;
    this.endDate = endDate;
  }

  public TransferStatus(Status status, LocalDateTime endDate, Exception exception) {
    this.status = status;
    this.endDate = endDate;
    this.exception = exception;
  }

  public static TransferStatus completed() {
    return new TransferStatus(Status.COMPLETED, LocalDateTime.now());
  }

  public static TransferStatus failed(Exception e) {
    return new TransferStatus(Status.FAILED, LocalDateTime.now(), e);
  }

  @Override
  public String toString() {
    String stringToBeReturned = status.toString();
    if (endDate != null)
      stringToBeReturned += ", " + endDate.toString();
    if (exception != null)
      stringToBeReturned += ", " + exception.toString();
    return stringToBeReturned;
  }
}
