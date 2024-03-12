package de.ume.deidentifhirpipeline.transfer.dataselection;

import de.ume.deidentifhirpipeline.api.data.Status;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.transfer.Context;

public abstract class DataSelection {
  public abstract void before(ProjectConfiguration projectConfiguration) throws Exception;
  public abstract Context process(Context context) throws Exception;

  public static void beforeExecution(ProjectConfiguration projectConfiguration) throws Exception {
    projectConfiguration.getDataSelectionImpl().before(projectConfiguration);
  }

  public static Context execute(Context context) {
    try {
      return context.getProjectConfiguration().getDataSelectionImpl().process(context);
    } catch (Exception e) {
      e.printStackTrace();
      context.setFailed(true);
      context.getTransfer().setStatus(Status.FAILED);
      context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.failed(e));
      return context;
    }
  }
}
