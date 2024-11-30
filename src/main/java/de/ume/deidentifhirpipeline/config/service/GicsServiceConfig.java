package de.ume.deidentifhirpipeline.config.service;

import de.ume.deidentifhirpipeline.config.auth.BasicAuthConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GicsServiceConfig {
  private String          mainWsdlUri;
  private BasicAuthConfig basic;
  private String          domain;
  private List<String>    policies;
}
