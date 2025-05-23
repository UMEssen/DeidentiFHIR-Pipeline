package de.ume.deidentifhirpipeline.transfer.lastupdated;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.lastupdated.LastUpdatedConfig;
import de.ume.deidentifhirpipeline.service.lastupdated.LastUpdatedServiceInterface;
import de.ume.deidentifhirpipeline.transfer.Context;
import org.springframework.stereotype.Component;

@Component("set-last-updated")
public class SetLastUpdatedImpl implements SetLastUpdated {

  public void before(ProjectConfig projectConfig) throws Exception {
    // Nothing to do
  }

  public Context process(Context context) throws Exception {
    LastUpdatedServiceInterface lastUpdatedService = context.getProjectConfig().getLastUpdated().getLastUpdatedService();
    if (context.getNewLastUpdated().isPresent()) {
      lastUpdatedService.setLastUpdatedValue(context.getPatientId(), context.getNewLastUpdated().getAsLong());
    }
    return context;
  }

}
