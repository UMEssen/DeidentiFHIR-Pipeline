package de.ume.deidentifhirpipeline.transfer.lastupdated;

import de.ume.deidentifhirpipeline.api.data.Status;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;

import java.util.Optional;

public interface GetLastUpdated {
  void before(ProjectConfig projectConfig) throws Exception;

  Context process(Context context) throws Exception;

  default void beforeExecution(ProjectConfig projectConfig) throws Exception {
    if (projectConfig.getLastUpdated() != null)
      this.before(projectConfig);
  }

  default Context execute(Context context) {
    try {
      if (context.getProjectConfig().getLastUpdated() != null) {
        return this.process(context);
      }
      return context;
    } catch (Exception e) {
      e.printStackTrace();
      context.setFailed(true);
      context.getTransfer().setStatus(Status.FAILED);
      context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.failed(e));
      return context;
    }
  }

}
