package de.ume.deidentifhirpipeline.configuration.pseudonymization;

import de.ume.deidentifhirpipeline.configuration.service.GpasServiceConfiguration;
import de.ume.deidentifhirpipeline.configuration.service.HashmapServiceConfiguration;
import de.ume.deidentifhirpipeline.service.pseudonymization.GpasService;
import de.ume.deidentifhirpipeline.service.pseudonymization.HashmapService;
import de.ume.deidentifhirpipeline.service.pseudonymization.PseudonymizationServiceInterface;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeidentiFHIRPseudonymizationConfiguration {
  @Getter
  @Setter
  private String scraperConfigFile;
  @Getter
  @Setter
  private String pseudonymizationConfigFile;
  @Getter
  private boolean generateIDScraperConfig;
  @Getter
  private long dateShiftingInMillis;
  @Getter
  @Setter
  private GpasServiceConfiguration gpas;
  @Getter
  @Setter
  private HashmapServiceConfiguration hashmap;
  @Setter
  private GpasService gpasService;
  @Setter
  private PseudonymizationServiceInterface pseudonymizationService;

  public DeidentiFHIRPseudonymizationConfiguration(String scraperConfigFile,
      String pseudonymizationConfigFile, boolean generateIDScraperConfig, long dateShiftingInMillis,
      GpasServiceConfiguration gpas, HashmapServiceConfiguration hashmap) {
    this.scraperConfigFile = scraperConfigFile;
    this.pseudonymizationConfigFile = pseudonymizationConfigFile;
    this.generateIDScraperConfig = generateIDScraperConfig;
    this.dateShiftingInMillis = dateShiftingInMillis;
    this.gpas = gpas;
    this.hashmap = hashmap;
  }

  public PseudonymizationServiceInterface getPseudonymizationService() {
    if( this.pseudonymizationService != null ) {
      return this.pseudonymizationService;
    } else if( hashmap != null ) {
      log.debug("Hashmap PseudonymizationService configured.");
      this.pseudonymizationService = new HashmapService(hashmap);
      return this.pseudonymizationService;
    } else if( gpas != null ) {
      log.debug("gPAS PseudonymizationService configured.");
      this.gpasService = new GpasService(gpas);
      return this.gpasService;
    } else {
      log.error("No PseudonymizationService (gpas, hashmap) found.");
      return null;
    }
  }
}
