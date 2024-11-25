package de.ume.deidentifhirpipeline.config.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicAuthConfig {
  private String user;
  private String password;

  public String toString() {
    return String.format("BasicAuthConfig(user=%s, password=%s", user, password);
  }
}
