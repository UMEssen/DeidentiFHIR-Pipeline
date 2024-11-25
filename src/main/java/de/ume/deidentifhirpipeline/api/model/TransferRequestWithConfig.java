package de.ume.deidentifhirpipeline.api.model;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import lombok.Getter;

public class TransferRequestWithConfig extends TransferRequest {
  @Getter
  ProjectConfig projectConfig;
}
