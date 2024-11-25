package de.ume.deidentifhirpipeline.transfer.lastupdated;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.lastupdated.LastUpdatedConfig;
import de.ume.deidentifhirpipeline.service.lastupdated.LastUpdatedServiceInterface;
import de.ume.deidentifhirpipeline.transfer.Context;

import java.util.OptionalLong;

public class GetLastUpdatedImpl extends GetLastUpdated {

  LastUpdatedConfig config;

  public GetLastUpdatedImpl(LastUpdatedConfig config) {
    this.config = config;
  }

  public void before(ProjectConfig projectConfig) throws Exception {
    LastUpdatedServiceInterface lastUpdatedService = config.getLastUpdatedService();
    if (lastUpdatedService != null) {
      config.getLastUpdatedService().createIfLastUpdatedDomainIsNotExistent();
    }
  }

  public Context process(Context context) throws Exception {
    LastUpdatedServiceInterface lastUpdatedService = config.getLastUpdatedService();
    if (lastUpdatedService != null) {
      long lastUpdated = lastUpdatedService.getLastUpdatedValue(context.getPatientId());
      context.setOldLastUpdated(OptionalLong.of(lastUpdated));
    }
    return context;
  }


}
