package de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentiFHIR;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.ume.deidentifhir.Deidentifhir;
import de.ume.deidentifhir.Registry;
import de.ume.deidentifhir.util.Handlers;
import de.ume.deidentifhir.util.JavaCompat;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Resource;
import scala.Function4;
import scala.collection.immutable.Map;
import scala.collection.immutable.Seq;

import java.io.File;

public class CDtoTransportDeidentiFHIR {

  private Deidentifhir deidentiFHIR;
  private PseudonymProvider ths = new PseudonymProvider();
  private DateShiftingProvider dsp = new DateShiftingProvider();

  public CDtoTransportDeidentiFHIR(File configFile) {

    Config config = ConfigFactory.parseFile(configFile);

    Registry registry = new Registry();
    registry.addHander("postalCodeHandler", Handlers.generalizePostalCodeHandler().get());
    registry.addHander("generalizeDateHandler",
        (Function4<Seq<String>, DateType, Seq<Base>, Map<String, String>, DateType>) Handlers::generalizeDateHandler);
    registry.addHander("PSEUDONYMISIERTstringReplacementHandler",
        JavaCompat.partiallyApply("PSEUDONYMISIERT", Handlers::stringReplacementHandler));
    registry.addHander("stringRedactedReplacementHandler",
        JavaCompat.partiallyApply("<redacted>", Handlers::stringReplacementHandler));
    registry.addHander("idReplacementHandler",
        JavaCompat.partiallyApply(ths, Handlers::idReplacementHandler));
    registry.addHander("referenceReplacementHandler",
        JavaCompat.partiallyApply(ths, Handlers::referenceReplacementHandler));
    registry.addHander("identifierValueReplacementHandler",
        JavaCompat.partiallyApply2(ths, true, Handlers::identifierValueReplacementHandler));
    registry.addHander("conditionalReferencesReplacementHandler",
        JavaCompat.partiallyApply2(ths, ths, Handlers::conditionalReferencesReplacementHandler));
    registry.addHander("shiftDateHandler",
        JavaCompat.partiallyApply(dsp, Handlers::shiftDateHandler));
    registry.addHander("timeShiftHandler",
        JavaCompat.partiallyApply(dsp, Handlers::shiftDateHandler));

    deidentiFHIR = Deidentifhir.apply(config, registry);
  }

  public Resource deidentify(String patientID, String patientPrefix, Resource resource,
      java.util.Map<String, String> pseudonymMap, java.util.Map<String, Long> dateShiftValueMap) {
    ths.setPseudonymTableKeyCreator(new CDtoTransportKeyCreator(patientPrefix));
    ths.setPseudonymMap(pseudonymMap);
    dsp.setTimeShiftMap(dateShiftValueMap);
    scala.collection.immutable.Map<String, String> staticContext =
        new Map.Map1<>(Handlers.patientIdentifierKey(), patientID);
    return deidentiFHIR.deidentify(resource, staticContext);
  }
}
