package de.ume.deidentifhirpipeline.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
  @Getter
  @Setter
  String transferId;
}
