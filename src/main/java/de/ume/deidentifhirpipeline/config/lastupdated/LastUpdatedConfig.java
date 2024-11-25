package de.ume.deidentifhirpipeline.config.lastupdated;

import de.ume.deidentifhirpipeline.config.service.GpasServiceConfig;
import de.ume.deidentifhirpipeline.config.service.HashmapServiceConfig;
import de.ume.deidentifhirpipeline.service.lastupdated.LastUpdatedServiceInterface;
import de.ume.deidentifhirpipeline.service.pseudonymization.GpasService;
import de.ume.deidentifhirpipeline.service.pseudonymization.HashmapService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class LastUpdatedConfig {
  private String zoneId;
  private GpasServiceConfig gpas;
  private HashmapServiceConfig hashmap;
  private GpasService gpasService;
  private LastUpdatedServiceInterface lastUpdatedService;

  public LastUpdatedServiceInterface getLastUpdatedService() {
    if (this.lastUpdatedService != null) {
      return this.lastUpdatedService;
    } else if (hashmap != null) {
      log.debug("Hashmap LastUpdatedService configured.");
      this.lastUpdatedService = new HashmapService(hashmap);
      return this.lastUpdatedService;
    } else if (gpas != null) {
      log.debug("gPAS LastUpdatedService configured.");
      this.gpasService = new GpasService(gpas);
      return this.gpasService;
    } else {
      log.error("No LastUpdatedService (gpas, hashmap) configured.");
      return null;
    }
  }

}
