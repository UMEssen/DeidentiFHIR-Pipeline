package de.ume.deidentifhirpipeline.transfer.datastoring;

import de.ume.deidentifhirpipeline.api.data.Status;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;

public interface DataStoring {
  void before(ProjectConfig projectConfig) throws Exception;

  void process(Context context);

  default void beforeExecution(ProjectConfig projectConfig) throws Exception {
    this.before(projectConfig);
  }

  default void execute(Context context) {
    try {
      this.process(context);
    } catch (Exception e) {
      e.printStackTrace();
      context.setFailed(true);
      context.getTransfer().setStatus(Status.FAILED);
      context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.failed(e));
    }
  }
}
