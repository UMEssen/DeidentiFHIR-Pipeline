package de.ume.deidentifhirpipeline.configuration.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeycloakAuthConfiguration {
  private String tokenUrl;
  private String username;
  private String password;
  private String clientId;
  private String clientSecret;
}
