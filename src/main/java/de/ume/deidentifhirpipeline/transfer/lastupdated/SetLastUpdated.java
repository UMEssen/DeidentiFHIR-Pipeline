package de.ume.deidentifhirpipeline.transfer.lastupdated;

import de.ume.deidentifhirpipeline.api.data.Status;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;

import java.util.Optional;

public abstract class SetLastUpdated {
  public abstract void before(ProjectConfig projectConfig) throws Exception;

  public abstract Context process(Context context) throws Exception;

  public static void beforeExecution(ProjectConfig projectConfig) throws Exception {
    Optional<SetLastUpdated> setLastUpdated = projectConfig.getSetLastUpdatedImpl();
    if (setLastUpdated.isPresent()) {
      setLastUpdated.get().before(projectConfig);
    }
  }

  public static Context execute(Context context) {
    try {
      Optional<SetLastUpdated> setLastUpdated = context.getProjectConfig().getSetLastUpdatedImpl();
      if (setLastUpdated.isPresent()) {
        return setLastUpdated.get().process(context);
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
