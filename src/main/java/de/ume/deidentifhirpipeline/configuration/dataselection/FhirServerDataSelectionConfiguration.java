package de.ume.deidentifhirpipeline.configuration.dataselection;

import de.ume.deidentifhirpipeline.configuration.auth.BasicAuthConfiguration;
import de.ume.deidentifhirpipeline.configuration.auth.TokenAuthConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FhirServerDataSelectionConfiguration {
  private String url;
  private String fhirIdQuery;
  private String fhirIdQueryPlaceholder;
  private String bundleQueryLastUpdatedPlaceholder;
  private String bundleQuery;
  private String bundleQueryPlaceholder;
  private BasicAuthConfiguration basicAuth;
  private TokenAuthConfiguration tokenAuth;
}
