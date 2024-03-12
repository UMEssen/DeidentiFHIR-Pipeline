package de.ume.deidentifhirpipeline.configuration.auth;

import lombok.Getter;
import lombok.Setter;

public class BasicAuthConfiguration {
  @Getter
  @Setter
  private String user;
  @Getter
  @Setter
  private String password;

  public String toString() {
    return String.format("BasicAuthConfiguration(user=%s, password=%s", user, password);
  }
}
