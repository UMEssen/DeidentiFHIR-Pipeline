package de.ume.deidentifhirpipeline.transfer;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import de.ume.deidentifhirpipeline.api.data.Status;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.config.auth.BasicAuthConfig;
import de.ume.deidentifhirpipeline.config.auth.TokenAuthConfig;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Utils {
  public static final FhirContext fctx = FhirContext.forR4();

  public static final String VALUE_NOT_FOUND                 = "*** VALUE NOT FOUND ***";
  public static final String DATE_SHIFTING_DOMAIN_SUFFIX     = "-DATE_SHIFTING";
  public static final String DATE_SHIFTING_DOMAIN_VALUE      = "___DATE-SHIFTING-IN-MILLIS___";
  public static final int    DATE_SHIFTING_DOMAIN_PSN_LENGTH = 18;
  public static final String DATE_SHIFTING_DELIMITER         = "X";
  public static final String LAST_UPDATED_DOMAIN_SUFFIX      = "-LAST_UPDATED";
  public static final int    LAST_UPDATED_DOMAIN_PSN_LENGTH  = 32;
  public static final String LAST_UPDATED_DELIMITER          = "X";

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
    if (bundle == null)
      return "Bundle is empty";
    return fctx.newJsonParser().encodeResourceToString(bundle);
  }

  public static String fhirBundleToStringPrettyPrint(Bundle bundle) {
    return Utils.fctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
  }

  public static String fhirResourceToString(Resource resource) {
    IParser parser = Utils.fctx.newJsonParser();
    return parser.setPrettyPrint(true).encodeResourceToString(resource);
  }

  public static void handleException(Context context, Exception e) {
    context.setFailed(true);
    context.getTransfer().setStatus(Status.FAILED);
    context.getTransfer().getMap().put(context.getPatientId(), TransferStatus.failed(e));
    e.printStackTrace();
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

  public static IGenericClient hapiClient(String url) {
    fctx.getRestfulClientFactory().setConnectTimeout(60000);
    fctx.getRestfulClientFactory().setSocketTimeout(60000);
    fctx.getRestfulClientFactory().setConnectionRequestTimeout(60000);
    IGenericClient hapiClient = fctx.newRestfulGenericClient(url);
    hapiClient.registerInterceptor(new LoggingInterceptor(true));

    return hapiClient;
  }

  public static IGenericClient hapiClient(String url, String user, String password) {
    IGenericClient hapiClient = hapiClient(url);
    hapiClient.registerInterceptor(new BasicAuthInterceptor(user, password));

    return hapiClient;
  }

  public static IGenericClient hapiClient(String url, BasicAuthConfig basic) {
    return hapiClient(url, basic.getUser(), basic.getPassword());
  }

  public static IGenericClient hapiClient(String url, String token) {
    IGenericClient hapiClient = hapiClient(url);
    hapiClient.registerInterceptor(new BearerTokenAuthInterceptor(token));

    return hapiClient;
  }

  public static IGenericClient hapiClient(String url, TokenAuthConfig token) {
    return hapiClient(url, token.getToken());
  }


}
