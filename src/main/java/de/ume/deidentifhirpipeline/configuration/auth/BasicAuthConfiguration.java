package de.ume.deidentifhirpipeline.configuration.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicAuthConfiguration {
  private String user;
  private String password;

  public String toString() {
    return String.format("BasicAuthConfiguration(user=%s, password=%s", user, password);
  }
}
