package de.ume.deidentifhirpipeline.configuration.service;

import de.ume.deidentifhirpipeline.configuration.auth.BasicAuthConfiguration;
import de.ume.deidentifhirpipeline.configuration.auth.KeycloakAuthConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GpasServiceConfiguration {
  private String domain;
  private String gpasServiceWsdlUrl;
  private String domainServiceWsdlUrl;
  private BasicAuthConfiguration basic;
  private KeycloakAuthConfiguration keycloak;
}
