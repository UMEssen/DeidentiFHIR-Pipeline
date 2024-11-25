package de.ume.deidentifhirpipeline.transfer;

import de.ume.deidentifhirpipeline.api.data.Transfer;
import de.ume.deidentifhirpipeline.config.ProjectConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;

import java.util.OptionalLong;

@Getter
@NoArgsConstructor
public class Context {
  Transfer transfer;
  ProjectConfig projectConfig;
  String patientId;
  @Setter
  OptionalLong oldLastUpdated = OptionalLong.empty();
  @Setter
  OptionalLong newLastUpdated = OptionalLong.empty();
  @Setter
  Bundle bundle;
  @Setter
  boolean failed = false;
  @Setter
  Exception exception;

  public Context(Transfer transfer, ProjectConfig projectConfig, String patientId) {
    this.transfer = transfer;
    this.projectConfig = projectConfig;
    this.patientId = patientId;
  }

}
