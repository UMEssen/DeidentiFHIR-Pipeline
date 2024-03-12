package de.ume.deidentifhirpipeline.configuration.service;

import de.ume.deidentifhirpipeline.configuration.auth.BasicAuthConfiguration;
import de.ume.deidentifhirpipeline.configuration.auth.KeycloakAuthConfiguration;
import lombok.Getter;
import lombok.Setter;

public class GpasServiceConfiguration {
  @Getter
  @Setter
  private String domain;
  @Getter
  @Setter
  private String gpasServiceWsdlUrl;
  @Getter
  @Setter
  private String domainServiceWsdlUrl;
  @Getter
  @Setter
  private BasicAuthConfiguration basic;
  @Getter
  @Setter
  private KeycloakAuthConfiguration keycloak;

}
