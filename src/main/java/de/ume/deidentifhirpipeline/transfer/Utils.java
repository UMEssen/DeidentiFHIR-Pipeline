package de.ume.deidentifhirpipeline.transfer;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

public class Utils {
  public static final FhirContext fctx = FhirContext.forR4();

  public static final String VALUE_NOT_FOUND = "*** VALUE NOT FOUND ***";
  public static final String DATE_SHIFTING_DOMAIN_SUFFIX = "-DATE_SHIFTING";
  public static final String DATE_SHIFTING_DOMAIN_VALUE = "___DATE-SHIFTING-IN-MILLIS___";
  public static final int DATE_SHIFTING_DOMAIN_PSN_LENGTH = 18;
  public static final String DATE_SHIFTING_DELIMITER = "X";

  public static final String LAST_UPDATED_DOMAIN_SUFFIX = "-LAST_UPDATED";
  public static final int LAST_UPDATED_DOMAIN_PSN_LENGTH = 32;
  public static final String LAST_UPDATED_DELIMITER = "X";

  private Utils() {
    throw new IllegalStateException("Utility class");
  }

  public static String getDateShiftingDomainName(String domain) {
    return domain + DATE_SHIFTING_DOMAIN_SUFFIX;
  }

  public static String fhirBundleToString(Bundle bundle) {
    if( bundle == null ) return "Bundle is empty";
    return fctx.newJsonParser().encodeResourceToString(bundle);
  }

  public static String fhirBundleToStringPrettyPrint(Bundle bundle) {
    return Utils.fctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
  }

  public static String fhirResourceToString(Resource resource) {
    IParser parser = Utils.fctx.newJsonParser();
    return parser.setPrettyPrint(true).encodeResourceToString(resource);
  }

  public static Context handleException(Context context, Exception e) {
    context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.failed(e));
    context.setFailed(true);
    context.setException(e);
    e.printStackTrace();
    return context;
  }
}
