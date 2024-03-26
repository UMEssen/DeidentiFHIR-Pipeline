package de.ume.deidentifhirpipeline.transfer.pseudonymization;

import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.pseudonymization.DeidentiFHIRPseudonymizationConfiguration;
import de.ume.deidentifhirpipeline.service.pseudonymization.PseudonymizationServiceInterface;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentiFHIR.CDtoTransportDeidentiFHIR;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentiFHIR.CDtoTransportKeyCreator;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentiFHIR.IDATScraper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
public class DeidentiFHIRPseudonymization extends Pseudonymization {

  DeidentiFHIRPseudonymizationConfiguration configuration;

  public DeidentiFHIRPseudonymization(DeidentiFHIRPseudonymizationConfiguration configuration) {
    this.configuration = configuration;
  }

  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    PseudonymizationServiceInterface pseudonymizationService = configuration.getPseudonymizationService();
    pseudonymizationService.createIfDomainIsNotExistent();
    log.info("DateShiftingInMillis: {}", configuration.getDateShiftingInMillis());
    pseudonymizationService.createIfDateShiftingDomainIsNotExistent(configuration.getDateShiftingInMillis());
  }

  public Context process(Context context) {
    try {

      // Gather IDs
      File scraperConfigFile = new File(configuration.getScraperConfigFile());
      IDATScraper idScraper = new IDATScraper(scraperConfigFile, configuration.isGenerateIDScraperConfig());
      List<String> gatheredIDs = idScraper.gatherIDs(
          new CDtoTransportKeyCreator(context.getPatientId()), context.getBundle()
      ).stream().toList();

      // Get pseudonyms from gPAS
      PseudonymizationServiceInterface pseudonymizationService = configuration.getPseudonymizationService();
      Map<String, String> pseudonymMap = pseudonymizationService.getOrCreatePseudonyms(gatheredIDs);

      // Get date shifting values from gPAS
      Map<String, Long> dateShiftValueMap;
      if( configuration.getDateShiftingInMillis() != 0 ) {
        long dateShiftValue = pseudonymizationService.getDateShiftingValue(context.getPatientId());
        dateShiftValueMap = Map.of(context.getPatientId(), dateShiftValue);
      } else {
        dateShiftValueMap = Map.of();
      }

      // Replace IDs and get bundle
      File pseudonymizationConfigFile = new File(configuration.getPseudonymizationConfigFile());
      CDtoTransportDeidentiFHIR deidentiFHIR =
          new CDtoTransportDeidentiFHIR(pseudonymizationConfigFile);
      Bundle bundle = (Bundle) deidentiFHIR.deidentify(context.getPatientId(), context.getPatientId(), context.getBundle(), pseudonymMap, dateShiftValueMap);

      context.setBundle(bundle);
      return context;
    } catch (Exception e) {
      e.printStackTrace();
      return Utils.handleException(context, e);
    }
  }
}
