package de.ume.deidentifhirpipeline.config.dataselection;

import de.ume.deidentifhirpipeline.config.auth.BasicAuthConfig;
import de.ume.deidentifhirpipeline.config.auth.TokenAuthConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FhirServerDataSelectionConfig {
  private String          url;
  private String          fhirIdQuery;
  private String          fhirIdQueryPlaceholder;
  private String          bundleQueryLastUpdatedPlaceholder;
  private String          bundleQuery;
  private String          bundleQueryPlaceholder;
  private BasicAuthConfig basicAuth;
  private TokenAuthConfig tokenAuth;
}
