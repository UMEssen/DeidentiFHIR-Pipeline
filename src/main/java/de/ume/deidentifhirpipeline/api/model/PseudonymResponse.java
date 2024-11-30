package de.ume.deidentifhirpipeline.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class PseudonymResponse {
  @Getter
  @Setter Map<String, String> idPseudonymMap;
}
