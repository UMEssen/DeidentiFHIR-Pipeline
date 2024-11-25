package de.ume.deidentifhirpipeline.config.service;

import de.ume.deidentifhirpipeline.config.auth.BasicAuthConfig;
import de.ume.deidentifhirpipeline.config.auth.KeycloakAuthConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GpasServiceConfig {
  private String domain;
  private String gpasServiceWsdlUrl;
  private String domainServiceWsdlUrl;
  private BasicAuthConfig basic;
  private KeycloakAuthConfig keycloak;
}
