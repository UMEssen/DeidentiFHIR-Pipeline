package de.ume.deidentifhirpipeline.transfer.lastupdated;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.service.lastupdated.LastUpdatedServiceInterface;
import de.ume.deidentifhirpipeline.transfer.Context;
import org.springframework.stereotype.Component;

import java.util.OptionalLong;

@Component("get-last-updated")
public class GetLastUpdatedImpl implements GetLastUpdated {

  public void before(ProjectConfig projectConfig) throws Exception {
    LastUpdatedServiceInterface lastUpdatedService = projectConfig.getLastUpdated().getLastUpdatedService();
    if (lastUpdatedService != null) {
      lastUpdatedService.createIfLastUpdatedDomainIsNotExistent();
    }
  }

  public Context process(Context context) throws Exception {
    LastUpdatedServiceInterface lastUpdatedService = context.getProjectConfig().getLastUpdated().getLastUpdatedService();
    if (lastUpdatedService != null) {
      long lastUpdated = lastUpdatedService.getLastUpdatedValue(context.getPatientId());
      context.setOldLastUpdated(OptionalLong.of(lastUpdated));
    }
    return context;
  }


}
