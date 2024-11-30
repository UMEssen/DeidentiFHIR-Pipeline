package de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentifhir;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import de.ume.deidentifhir.Deidentifhir;
import de.ume.deidentifhir.Registry;
import de.ume.deidentifhir.util.Handlers;
import de.ume.deidentifhir.util.JavaCompat;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Slf4j
public class IDATScraper {
  private Deidentifhir    deidentiFHIR;
  private ScrapingStorage scrapingStorage;

  public IDATScraper(File configFile, boolean generateIDScraperConfig) {
    scrapingStorage = new ScrapingStorage();

    Config config = ConfigFactory.parseFile(configFile);
    if (generateIDScraperConfig)
      config = generateIDScraperConfig(config);

    Registry registry = new Registry();
    registry.addHander("gatherIdHandler",
        JavaCompat.partiallyApply(scrapingStorage, Handlers::idReplacementHandler));
    registry.addHander("gatherReferenceHandler",
        JavaCompat.partiallyApply(scrapingStorage, Handlers::referenceReplacementHandler));
    registry.addHander("gatherIdentifierValueHandler",
        JavaCompat.partiallyApply2(scrapingStorage, true, Handlers::identifierValueReplacementHandler));
    registry.addHander("gatherConditionalReferencesHandler",
        JavaCompat.partiallyApply2(scrapingStorage, scrapingStorage, Handlers::conditionalReferencesReplacementHandler));

    deidentiFHIR = Deidentifhir.apply(config, registry);
  }

  /**
   * Gather all IDs contained in the provided bundle and return them as a Set.
   *
   * To ensure thread safety this method is synchronized.
   */
  public synchronized Set<String> gatherIDs(PseudonymTableKeyCreator keyCreator, Bundle bundle) {
    scrapingStorage.setPseudonymTableKeyCreator(keyCreator);
    deidentiFHIR.deidentify(bundle);
    return scrapingStorage.getAndResetGatheredIDATs();
  }

  private Config generateIDScraperConfig(Config config) {
    Config newConfig = config;

    for (Map.Entry<String, ConfigValue> e : config.entrySet()) {
      Object value = e.getValue().unwrapped();

      // replace handlers
      if (value.equals("idReplacementHandler"))
        newConfig = newConfig.withValue(e.getKey(), ConfigValueFactory.fromAnyRef("gatherIdHandler"));
      else if (value.equals("referenceReplacementHandler"))
        newConfig = newConfig.withValue(e.getKey(), ConfigValueFactory.fromAnyRef("gatherReferenceHandler"));
      // remove handlers
      else if (value.equals("shiftDateHandler") ||
          value.equals("timeShiftHandler") ||
          value.equals("postalCodeHandler") ||
          value.equals("generalizeDateHandler") ||
          value.equals("PSEUDONYMISIERTstringReplacementHandler") ||
          value.equals("stringRedactedReplacementHandler")) {
        String path = getParent(e.getKey());
        newConfig = newConfig.withoutPath(path);
      }
    }

    log.trace("Generated IDScraper config: ");
    newConfig.entrySet().forEach(e -> log.trace(e.getKey() + ", " + e.getValue().render()));
    return newConfig;
  }

  private String getParent(String path) {
    String[] arrayString = path.split("\\.");
    arrayString = Arrays.copyOfRange(arrayString, 0, arrayString.length - 1);
    return String.join(".", arrayString);
  }

}
