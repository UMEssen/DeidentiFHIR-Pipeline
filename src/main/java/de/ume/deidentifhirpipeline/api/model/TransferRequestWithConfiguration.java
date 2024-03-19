package de.ume.deidentifhirpipeline.api.model;

import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import lombok.Getter;

public class TransferRequestWithConfiguration extends TransferRequest{
  @Getter
  ProjectConfiguration projectConfiguration;
}
