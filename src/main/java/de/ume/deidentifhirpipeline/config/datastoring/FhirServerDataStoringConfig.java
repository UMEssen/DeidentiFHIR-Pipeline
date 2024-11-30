package de.ume.deidentifhirpipeline.config.datastoring;

import de.ume.deidentifhirpipeline.config.auth.BasicAuthConfig;
import de.ume.deidentifhirpipeline.config.auth.TokenAuthConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FhirServerDataStoringConfig {
  private String          url;
  private BasicAuthConfig basicAuth;
  private TokenAuthConfig tokenAuth;

  public String toString() {
    return String.format("DataStoringConfig(url=%s)", url);
  }
}
