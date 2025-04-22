package de.ume.deidentifhirpipeline.transfer.pseudonymization;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.transfer.Context;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("pseudonymization.none")
public class NoPseudonymization implements Pseudonymization {

  public void before(ProjectConfig projectConfig) throws Exception {}

  public void process(Context context) {
    // Nothing to do.
  }
}
