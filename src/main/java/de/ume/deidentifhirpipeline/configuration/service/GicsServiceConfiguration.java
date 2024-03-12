package de.ume.deidentifhirpipeline.configuration.service;

import de.ume.deidentifhirpipeline.configuration.auth.BasicAuthConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class GicsServiceConfiguration {
  @Getter
  @Setter
  private String mainWsdlUri;
  @Getter
  @Setter
  private BasicAuthConfiguration basic;
  @Getter
  @Setter
  String domain;
  @Getter
  @Setter
  private List<String> policies;

}
