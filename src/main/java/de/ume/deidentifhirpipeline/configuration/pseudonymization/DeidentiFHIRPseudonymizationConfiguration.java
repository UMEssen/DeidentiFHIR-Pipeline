package de.ume.deidentifhirpipeline.configuration.pseudonymization;

import de.ume.deidentifhirpipeline.configuration.service.GpasServiceConfiguration;
import de.ume.deidentifhirpipeline.service.GpasService;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URISyntaxException;

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
  @Setter
  private GpasService gpasService;

  public DeidentiFHIRPseudonymizationConfiguration(String scraperConfigFile,
      String pseudonymizationConfigFile, boolean generateIDScraperConfig, long dateShiftingInMillis, GpasServiceConfiguration gpas) throws Exception {
    this.scraperConfigFile = scraperConfigFile;
    this.pseudonymizationConfigFile = pseudonymizationConfigFile;
    this.generateIDScraperConfig = generateIDScraperConfig;
    this.dateShiftingInMillis = dateShiftingInMillis;
    this.gpas = gpas;
  }

  public GpasService getGpasService() throws IOException, URISyntaxException {
    if( gpasService != null ) {
      return gpasService;
    } else if( gpas != null ) {
      this.gpasService = new GpasService(gpas);
      return this.gpasService;
    } else {
      return null;
    }
  }
}
