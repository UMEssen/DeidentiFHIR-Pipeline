package de.ume.deidentifhirpipeline.config.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeycloakAuthConfig {
  private String tokenUrl;
  private String username;
  private String password;
  private String clientId;
  private String clientSecret;
}
