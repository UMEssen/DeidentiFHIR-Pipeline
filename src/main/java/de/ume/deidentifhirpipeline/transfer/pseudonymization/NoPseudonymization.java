package de.ume.deidentifhirpipeline.transfer.pseudonymization;

import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.transfer.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoPseudonymization extends Pseudonymization {

  public void before(ProjectConfiguration projectConfiguration) throws Exception {}

  public Context process(Context context) {
    return context;
  }
}
