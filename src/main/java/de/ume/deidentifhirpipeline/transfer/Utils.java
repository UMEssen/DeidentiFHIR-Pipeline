package de.ume.deidentifhirpipeline.transfer;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

  public static String getLastUpdatedDomainName(String domain) {
    return domain + LAST_UPDATED_DOMAIN_SUFFIX;
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

  public static ZonedDateTime longToZonedDateTime(long millis, ZoneId zoneId) {
    Instant instant = Instant.ofEpochMilli(millis);
    return ZonedDateTime.ofInstant(instant, zoneId);
  }

  public static String zonedDateTimeToFhirSearchString(ZonedDateTime zonedDateTime) {
    int year = zonedDateTime.getYear();
    int month = zonedDateTime.getMonthValue();
    int day = zonedDateTime.getDayOfMonth();
    int hour = zonedDateTime.getHour();
    int minute = zonedDateTime.getMinute();
    int second = zonedDateTime.getSecond();

    return String.format("%s-%02d-%02dT%02d:%02d:%02d", year, month, day, hour, minute, second);
  }

  public static String longToFiremetricsDateString(long inMillis, ZoneId zoneId) {
    ZonedDateTime zonedDateTime = longToZonedDateTime(inMillis, zoneId);
    int year = zonedDateTime.getYear();
    int month = zonedDateTime.getMonthValue();
    int day = zonedDateTime.getDayOfMonth();
    int hour = zonedDateTime.getHour();
    int minute = zonedDateTime.getMinute();
    int second = zonedDateTime.getSecond();
    int millis = zonedDateTime.getNano() / 1_000_000;

    return String.format("%s-%02d-%02d %02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millis);
  }
}
