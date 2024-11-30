package de.ume.deidentifhirpipeline.config.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HashmapServiceConfig {
  private String  domain;
  private boolean exportHashmapAfterTransfer;
}
