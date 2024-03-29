package de.ume.deidentifhirpipeline.configuration.service;

import de.ume.deidentifhirpipeline.configuration.auth.BasicAuthConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GicsServiceConfiguration {
  private String mainWsdlUri;
  private BasicAuthConfiguration basic;
  String domain;
  private List<String> policies;
}
