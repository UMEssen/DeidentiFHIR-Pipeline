package de.ume.deidentifhirpipeline.configuration.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenAuthConfiguration {
  private String token;

  public String toString() {
    return String.format("BasicAuthConfiguration(token=%s", token);
  }
}
