package de.ume.deidentifhirpipeline.transfer.pseudonymization;

import de.ume.deidentifhirpipeline.api.data.Status;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;

public abstract class Pseudonymization {
  public abstract void before(ProjectConfig projectConfig) throws Exception;

  public abstract Context process(Context context);

  public static void beforeExecution(ProjectConfig projectConfig) throws Exception {
    projectConfig.getPseudonymizationImpl().before(projectConfig);
  }

  public static Context execute(Context context) {
    try {
      return context.getProjectConfig().getPseudonymizationImpl().process(context);
    } catch (Exception e) {
      e.printStackTrace();
      context.setFailed(true);
      context.getTransfer().setStatus(Status.FAILED);
      context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.failed(e));
      return context;
    }
  }
}
