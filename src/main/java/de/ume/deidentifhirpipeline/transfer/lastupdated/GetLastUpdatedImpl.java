package de.ume.deidentifhirpipeline.transfer.lastupdated;

import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.lastupdated.LastUpdatedConfiguration;
import de.ume.deidentifhirpipeline.service.lastupdated.LastUpdatedServiceInterface;
import de.ume.deidentifhirpipeline.transfer.Context;

import java.util.OptionalLong;

public class GetLastUpdatedImpl extends GetLastUpdated {

  LastUpdatedConfiguration configuration;

  public GetLastUpdatedImpl(LastUpdatedConfiguration configuration) {
    this.configuration = configuration;
  }

  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    LastUpdatedServiceInterface lastUpdatedService = configuration.getLastUpdatedService();
    if( lastUpdatedService != null ) {
      configuration.getLastUpdatedService().createIfLastUpdatedDomainIsNotExistent();
    }
  }
  public Context process(Context context) throws Exception {
    LastUpdatedServiceInterface lastUpdatedService = configuration.getLastUpdatedService();
    if( lastUpdatedService != null ) {
      long lastUpdated = lastUpdatedService.getLastUpdatedValue(context.getPatientId());
      context.setOldLastUpdated(OptionalLong.of(lastUpdated));
    }
    return context;
  }


}
