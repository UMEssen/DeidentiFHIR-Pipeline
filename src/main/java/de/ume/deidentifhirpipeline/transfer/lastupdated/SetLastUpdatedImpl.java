package de.ume.deidentifhirpipeline.transfer.lastupdated;

import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.lastupdated.LastUpdatedConfiguration;
import de.ume.deidentifhirpipeline.service.lastupdated.LastUpdatedServiceInterface;
import de.ume.deidentifhirpipeline.transfer.Context;

public class SetLastUpdatedImpl extends SetLastUpdated {

  LastUpdatedConfiguration configuration;

  public SetLastUpdatedImpl(LastUpdatedConfiguration configuration) {
    this.configuration = configuration;
  }

  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    // Nothing to do
  }
  public Context process(Context context) throws Exception {
    LastUpdatedServiceInterface lastUpdatedService = configuration.getLastUpdatedService();
    if( context.getNewLastUpdated().isPresent() ) {
      lastUpdatedService.setLastUpdatedValue(context.getPatientId(), context.getNewLastUpdated().getAsLong());
    }
    return context;
  }

}
