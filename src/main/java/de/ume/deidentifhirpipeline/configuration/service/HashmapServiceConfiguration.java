package de.ume.deidentifhirpipeline.configuration.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HashmapServiceConfiguration {
  private String domain;
  private boolean exportHashmapAfterTransfer;
}
