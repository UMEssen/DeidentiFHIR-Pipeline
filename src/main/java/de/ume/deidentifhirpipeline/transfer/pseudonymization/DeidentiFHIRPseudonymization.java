package de.ume.deidentifhirpipeline.transfer.pseudonymization;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.pseudonymization.DeidentiFHIRPseudonymizationConfig;
import de.ume.deidentifhirpipeline.service.pseudonymization.PseudonymizationServiceInterface;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentifhir.CDtoTransportDeidentiFHIR;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentifhir.CDtoTransportKeyCreator;
import de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentifhir.IDATScraper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("pseudonymization.deidentifhir")
public class DeidentiFHIRPseudonymization implements Pseudonymization {

  public void before(ProjectConfig projectConfig) throws Exception {
    DeidentiFHIRPseudonymizationConfig config = projectConfig.getPseudonymization().getDeidentifhir();
    PseudonymizationServiceInterface pseudonymizationService = config.getPseudonymizationService();
    pseudonymizationService.createIfDomainIsNotExistent();
    log.info("DateShiftingInMillis: {}", config.getDateShiftingInMillis());
    pseudonymizationService.createIfDateShiftingDomainIsNotExistent(config.getDateShiftingInMillis());
  }

  public Bundle process(Context context) throws Exception {
    DeidentiFHIRPseudonymizationConfig config = context.getProjectConfig().getPseudonymization().getDeidentifhir();
    // Gather IDs
    File scraperConfigFile = new File(config.getScraperConfigFile());
    IDATScraper idScraper = new IDATScraper(scraperConfigFile, config.isGenerateIDScraperConfig());
    List<String> gatheredIDs = idScraper.gatherIDs(
        new CDtoTransportKeyCreator(context.getPatientId()), context.getBundle()).stream().toList();

    // Get pseudonyms from gPAS
    PseudonymizationServiceInterface pseudonymizationService = config.getPseudonymizationService();
    Map<String, String> pseudonymMap = pseudonymizationService.getOrCreatePseudonyms(gatheredIDs);

    // Get date shifting values from gPAS
    Map<String, Long> dateShiftValueMap;
    if (config.getDateShiftingInMillis() != 0) {
      long dateShiftValue = pseudonymizationService.getDateShiftingValue(context.getPatientId());
      dateShiftValueMap = Map.of(context.getPatientId(), dateShiftValue);
    } else {
      dateShiftValueMap = Map.of();
    }

    // Replace IDs and get bundle
    File pseudonymizationConfigFile = new File(config.getPseudonymizationConfigFile());
    CDtoTransportDeidentiFHIR deidentiFHIR =
        new CDtoTransportDeidentiFHIR(pseudonymizationConfigFile);
    Bundle bundle =
        (Bundle) deidentiFHIR.deidentify(context.getPatientId(), context.getPatientId(), context.getBundle(), pseudonymMap, dateShiftValueMap);

    return bundle;
  }
}
