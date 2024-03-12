package de.ume.deidentifhirpipeline.transfer;

import de.ume.deidentifhirpipeline.api.data.Transfer;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Context {
  Transfer transfer;
  ProjectConfiguration projectConfiguration;
  String patientId;
  @Setter
  Bundle bundle;
  @Setter
  boolean failed = false;
  @Setter
  Exception exception;

}
