package de.ume.deidentifhirpipeline.config.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenAuthConfig {
  private String token;

  public String toString() {
    return String.format("BasicAuthConfig(token=%s", token);
  }
}
