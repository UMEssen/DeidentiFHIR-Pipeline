package de.ume.deidentifhirpipeline.configuration.datastoring;

import de.ume.deidentifhirpipeline.configuration.auth.BasicAuthConfiguration;
import de.ume.deidentifhirpipeline.configuration.auth.TokenAuthConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FhirServerDataStoringConfiguration {
  private String url;
  private BasicAuthConfiguration basicAuth;
  private TokenAuthConfiguration tokenAuth;

  public String toString() {
    return String.format("DataStoringConfiguration(url=%s)",url);
  }
}
