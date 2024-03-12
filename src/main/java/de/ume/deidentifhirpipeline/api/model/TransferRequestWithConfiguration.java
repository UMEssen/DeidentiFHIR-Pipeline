package de.ume.deidentifhirpipeline.api.model;

import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import lombok.Getter;

import java.util.List;

public class TransferRequestWithConfiguration extends TransferRequest{
  @Getter
  ProjectConfiguration projectConfiguration;
}
