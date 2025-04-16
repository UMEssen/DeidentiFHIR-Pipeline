package de.ume.deidentifhirpipeline.service.pseudonymization;

import de.ume.deidentifhirpipeline.config.auth.KeycloakAuthConfig;
import de.ume.deidentifhirpipeline.config.service.GpasServiceConfig;
import de.ume.deidentifhirpipeline.service.KeycloakService;
import de.ume.deidentifhirpipeline.service.exception.AddDomainException;
import de.ume.deidentifhirpipeline.service.exception.GetDomainException;
import de.ume.deidentifhirpipeline.service.exception.InsertValuePseudonymPairException;
import de.ume.deidentifhirpipeline.service.exception.TokenException;
import de.ume.deidentifhirpipeline.service.gpas.SoapTemplates;
import de.ume.deidentifhirpipeline.service.lastupdated.LastUpdatedServiceInterface;
import de.ume.deidentifhirpipeline.transfer.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GpasService implements PseudonymizationServiceInterface, LastUpdatedServiceInterface {

  private final GpasServiceConfig  config;
  private final KeycloakAuthConfig keycloakConfig;
  private final String             domain;
  private final String             dateShiftingDomain;
  private final String             lastUpdatedDomain;
  private static String            token;
  private final int                numberOfRetries = 2;

  public GpasService(GpasServiceConfig config) {
    this.config             = config;
    this.domain             = config.getDomain();
    this.dateShiftingDomain = Utils.getDateShiftingDomainName(this.domain);
    this.lastUpdatedDomain  = Utils.getLastUpdatedDomainName(this.domain);
    this.keycloakConfig     = config.getKeycloak();
  }

  @Override
  public Map<String, String> getOrCreatePseudonyms(List<String> ids) throws Exception {
    return this.getOrCreatePseudonymForListClient(ids, this.domain);
  }

  private Map<String, String> getOrCreatePseudonymForListClient(List<String> values, String domainName) throws Exception {
    return this.getOrCreatePseudonymForListClient(values, domainName, this.numberOfRetries);
  }

  private Map<String, String> getOrCreatePseudonymForListClient(List<String> values, String domainName, int numberOfRetries) throws Exception {
    if (values.isEmpty())
      return Map.of();
    if (numberOfRetries <= 0)
      throw new Exception("Token refresh failed");

    HttpResponse<String> httpResponse = gpasServiceRequest(SoapTemplates.getOrCreatePseudonymForListXmlString(values, domainName));
    log.debug("Received from gPAS: " + httpResponse.body());

    if (httpResponse.statusCode() == 200) {
      log.debug("200 from gPAS");
      return getValuesAndPseudonymsFromXml(httpResponse.body());
    } else {
      if (httpResponse.body().contains("UnknownValueException")) {
        log.debug("Could not get pseudonyms from gPAS.");
        return null;
      } else {
        throw new Exception("Could not parse response from gPAS");
      }
    }
  }

  @Override
  public long getDateShiftingValue(String id) throws Exception {
    long rangeInMillis = Long.parseLong(this.getPseudonymClient(Utils.DATE_SHIFTING_DOMAIN_VALUE, this.dateShiftingDomain));
    return this.getDateShiftingValue(id, rangeInMillis);
  }

  private long getDateShiftingValue(String id, long rangeInMillis) throws Exception {

    String value = getPseudonymClient(id, this.dateShiftingDomain);

    if (value == null) {
      long pseudonym = this.getRandomLong(0, rangeInMillis);
      this.insertDateShiftingValue(id, pseudonym, 0L);
      return calculateDateShiftingValue(pseudonym, rangeInMillis);
    } else {
      long pseudonym = Long.parseLong(extractDateShiftingValue(value));
      return calculateDateShiftingValue(pseudonym, rangeInMillis);
    }

  }


  @Override
  public synchronized void createIfDomainIsNotExistent() throws Exception {
    if (!this.isDomainExistingViaHttpClient(this.domain))
      this.addDomainViaHttpClient(this.domain);
  }

  @Override
  public synchronized void createIfLastUpdatedDomainIsNotExistent() throws Exception {
    if (!this.isDomainExistingViaHttpClient(this.lastUpdatedDomain))
      this.addLastUpdatedDomainViaHttpClient(this.lastUpdatedDomain);
  }

  @Override
  public synchronized void createIfDateShiftingDomainIsNotExistent(long millis) throws Exception {
    if (!this.isDomainExistingViaHttpClient(this.dateShiftingDomain)) {
      this.addDateShiftingDomainViaHttpClient(this.dateShiftingDomain);
      try {
        String millisWithCorrectLength = this.buildDomainDateShiftingPseudonym(millis);
        log.debug("Milliscorrect: " + millisWithCorrectLength);
        this.insertValuePseudonymPairViaHttpClient(Utils.DATE_SHIFTING_DOMAIN_VALUE, millisWithCorrectLength, this.dateShiftingDomain);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private long calculateDateShiftingValue(long value, long rangeInMillis) {
    return value - (rangeInMillis / 2);
  }

  private void insertValuePseudonymPairViaHttpClient(String id, String pseudonymString, String domainName)
      throws Exception {
    HttpResponse<String> httpResponse = gpasServiceRequest(SoapTemplates.getInsertValuePseudonymPairXmlString(id, pseudonymString, domainName));
    if (httpResponse.statusCode() != 200 && httpResponse.body().contains("InsertPairException"))
      throw new InsertValuePseudonymPairException(httpResponse.body());
    if (httpResponse.statusCode() != 200)
      throw new Exception("Could not insert pseudonym.\n" + httpResponse.body());
  }

  private void insertDateShiftingValue(String id, long value, long x)
      throws Exception {
    String pseudonymString = buildDateShiftingPseudonym(x, String.valueOf(value));
    try {
      this.insertValuePseudonymPairViaHttpClient(id, pseudonymString, this.dateShiftingDomain);
    } catch (InsertValuePseudonymPairException e) {
      x++;
      this.insertDateShiftingValue(id, value, x);
    }
  }

  private static String buildDateShiftingPseudonym(long duplicate, String dateShiftingInMillis) {
    int length = dateShiftingInMillis.length();
    int lengthOfDuplicate = String.valueOf(duplicate).length();
    int diff = Utils.DATE_SHIFTING_DOMAIN_PSN_LENGTH - length - lengthOfDuplicate - 1;
    dateShiftingInMillis = duplicate + Utils.DATE_SHIFTING_DELIMITER + dateShiftingInMillis;
    for (int i = 0; i < diff; i++) {
      dateShiftingInMillis = "0" + dateShiftingInMillis;
    }
    return dateShiftingInMillis;
  }

  private static String extractDateShiftingValue(String value) {
    return value.split(Utils.DATE_SHIFTING_DELIMITER)[1];
  }

  private long getRandomLong(long lower, long upper) {
    return new RandomDataGenerator().nextLong(lower, upper);
  }

  private static String buildDomainDateShiftingPseudonym(Long dateShiftingInMillis) throws Exception {
    String dateShiftingInMillisString = String.valueOf(dateShiftingInMillis);
    int length = dateShiftingInMillisString.length();
    int diff = Utils.DATE_SHIFTING_DOMAIN_PSN_LENGTH - length;
    if (diff < 0)
      throw new Exception("dateShifting value is too large");
    for (int i = 0; i < diff; i++) {
      dateShiftingInMillisString = "0" + dateShiftingInMillisString;
    }
    return dateShiftingInMillisString;
  }

  private boolean isDomainExistingViaHttpClient(String domain) throws Exception {
    HttpResponse<String> httpResponse = this.gpasDomainRequest(SoapTemplates.getDomainXmlString(domain));
    if (httpResponse.statusCode() == 200)
      return true;
    else if (httpResponse.statusCode() == 500 && httpResponse.body().contains("UnknownDomainException"))
      return false;
    else {
      throw new GetDomainException(httpResponse.body());
    }
  }

  private void addDomainViaHttpClient(String domain) throws Exception {
    String soapString = SoapTemplates.addDomainXmlString(
        domain,
        domain,
        "org.emau.icmvc.ganimed.ttp.psn.generator.HammingCode",
        "org.emau.icmvc.ganimed.ttp.psn.alphabets.Symbol32",
        "DEFAULT",
        "false",
        "false",
        "2",
        "12",
        "true",
        "0",
        "auto generated TMP domain");
    HttpResponse<String> httpResponse = this.gpasDomainRequest(soapString);
    if (httpResponse.statusCode() != 200)
      throw new AddDomainException(httpResponse.body());
  }

  private void addDateShiftingDomainViaHttpClient(String domain) throws Exception {
    HttpResponse<String> httpResponse = this.gpasDomainRequest(SoapTemplates.addDomainXmlString(
        domain,
        domain,
        "org.emau.icmvc.ganimed.ttp.psn.generator.NoCheckDigits",
        "org.emau.icmvc.ganimed.ttp.psn.alphabets.NumbersX",
        "DEFAULT",
        "false",
        "false",
        "2",
        String.valueOf(Utils.DATE_SHIFTING_DOMAIN_PSN_LENGTH),
        "true",
        "0",
        "auto generated date shifting domain"));
    if (httpResponse.statusCode() != 200)
      throw new AddDomainException(httpResponse.body());
  }

  private void addLastUpdatedDomainViaHttpClient(String domain) throws Exception {
    HttpResponse<String> httpResponse = this.gpasDomainRequest(SoapTemplates.addDomainXmlString(
        domain,
        domain,
        "org.emau.icmvc.ganimed.ttp.psn.generator.NoCheckDigits",
        "org.emau.icmvc.ganimed.ttp.psn.alphabets.NumbersX",
        "DEFAULT",
        "false",
        "false",
        "2",
        String.valueOf(Utils.LAST_UPDATED_DOMAIN_PSN_LENGTH),
        "true",
        "0",
        "auto generated lastUpdated domain"));
    if (httpResponse.statusCode() != 200)
      throw new AddDomainException(httpResponse.body());
  }

  @Override
  public long getLastUpdatedValue(String id) throws Exception {
    return getLastUpdated(id, this.lastUpdatedDomain);
  }

  @Override
  public void setLastUpdatedValue(String id, long lastUpdated) throws Exception {
    setLastUpdate(id, lastUpdated);
  }

  public long getLastUpdated(String id, String domain) throws Exception {
    log.debug("getLastUpdated called.");
    String response = getPseudonymClient(id, domain);
    if (response == null) {
      // create first lastUpdated value
      ZonedDateTime zdt = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
      long inMillis = zdt.toInstant().toEpochMilli();
      return inMillis;
    } else {
      return gpasStringtoLong(response);
    }
  }

  private long gpasStringtoLong(String response) {
    return Long.parseLong(response.split(Utils.DATE_SHIFTING_DELIMITER)[1]);
  }

  private void setLastUpdate(String id, long lastUpdated) throws Exception {
    this.deleteEntry(id, this.lastUpdatedDomain);
    setLastUpdate(id, lastUpdated, 0);
  }

  private void setLastUpdate(String id, long lastUpdated, int x) throws Exception {
    this.deleteEntry(id, domain);
    String generatedPseudonym = longToGpasString(lastUpdated, x);
    try {
      this.insertValuePseudonymPairViaHttpClient(id, generatedPseudonym, this.lastUpdatedDomain);
    } catch (InsertValuePseudonymPairException e) {
      x++;
      this.setLastUpdate(id, lastUpdated, x);
    }
  }

  private String longToGpasString(long value, int duplicate) throws Exception {
    String dateShiftingInMillisString = duplicate + Utils.LAST_UPDATED_DELIMITER + value;
    int length = dateShiftingInMillisString.length();
    int diff = Utils.LAST_UPDATED_DOMAIN_PSN_LENGTH - length;
    if (diff < 0)
      throw new Exception("dateShifting value is too large");
    for (int i = 0; i < diff; i++) {
      dateShiftingInMillisString = "0" + dateShiftingInMillisString;
    }
    return dateShiftingInMillisString;
  }

  private String getPseudonymClient(String value, String domainName) throws Exception {
    return this.getPseudonymClient(value, domainName, this.numberOfRetries);
  }

  private String getPseudonymClient(String value, String domainName, int numberOfRetries) throws Exception {
    if (numberOfRetries <= 0)
      throw new Exception("Token refresh failed");

    HttpResponse<String> httpResponse = gpasServiceRequest(SoapTemplates.getPseudonymForXmlString(value, domainName));

    if (httpResponse.statusCode() == 200) {
      log.debug("200 date shift value already in gPAS");
      return getFullNameFromXml(httpResponse.body(), "psn");
    } else {
      if (httpResponse.body().contains("UnknownValueException")) {
        log.debug("No dateShift value found.");
        return null;
      } else {
        throw new Exception("Could not parse response from gPAS");
      }
    }

  }

  private static Document loadXMLString(String response) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(response));

    return db.parse(is);
  }

  private static String getFullNameFromXml(String response, String tagName) throws Exception {
    Document xmlDoc = loadXMLString(response);
    NodeList nodeList = xmlDoc.getElementsByTagName(tagName);
    if (nodeList.getLength() != 0) {
      Node x = nodeList.item(0);
      log.debug("Raw date shift value: " + x.getFirstChild().getNodeValue());
      return x.getFirstChild().getNodeValue();
    }
    return null;
  }

  private static Map<String, String> getValuesAndPseudonymsFromXml(String response) throws Exception {
    Map<String, String> mapToBeReturned = new HashMap<>();
    Document xmlDoc = loadXMLString(response);
    NodeList nodeList = xmlDoc.getElementsByTagName("entry");
    if (nodeList.getLength() != 0) {
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        String key = "";
        String value = "";
        for (int j = 0; j < node.getChildNodes().getLength(); j++) {
          Node keyOrValue = node.getChildNodes().item(j);
          if (keyOrValue.getNodeName().equals("key"))
            key = keyOrValue.getTextContent();
          else
            value = keyOrValue.getTextContent();
        }
        mapToBeReturned.put(key, value);
      }
      return mapToBeReturned;
    }
    return null;
  }

  private void deleteEntry(String value, String domainName)
      throws TokenException, IOException, InterruptedException {
    gpasServiceRequest(SoapTemplates.deleteEntry(value, domainName));
  }

  private HttpResponse<String> gpasServiceRequest(String body) throws IOException, InterruptedException, TokenException {
    return gpasServiceRequest(config.getGpasServiceWsdlUrl(), body, this.numberOfRetries);
  }

  private HttpResponse<String> gpasDomainRequest(String body) throws IOException, InterruptedException, TokenException {
    return gpasServiceRequest(config.getDomainServiceWsdlUrl(), body, this.numberOfRetries);
  }

  private HttpResponse<String> gpasServiceRequest(String url, String body, int numberOfRetries)
      throws IOException, InterruptedException, TokenException {
    if (numberOfRetries <= 0)
      throw new TokenException();

    HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
    if (config.getBasic() != null) {
      httpClientBuilder.authenticator(new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(config.getBasic().getUser(), config.getBasic().getPassword().toCharArray());
        }
      });
    }

    try (HttpClient httpClient = httpClientBuilder.build()) {

      HttpRequest.Builder httpRequestBuilder =
          HttpRequest.newBuilder().uri(URI.create(url))
              .header("Content-Type", "application/soap+xml")
              .POST(HttpRequest.BodyPublishers.ofString(body));

      if (keycloakConfig != null)
        httpRequestBuilder.header("Authorization", "Bearer " + this.token);

      HttpRequest httpRequest = httpRequestBuilder.build();

      HttpResponse<String> httpResponse =
          httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

      // check for OAuth error
      if (httpResponse.statusCode() == 500 && httpResponse.body().contains("OAuth")) {
        // token = this.refreshToken();
        if (keycloakConfig != null)
          this.token = KeycloakService.getKeycloakToken(keycloakConfig);
        return gpasServiceRequest(url, body, numberOfRetries - 1);
      }

      return httpResponse;
    }
  }

  // private String refreshToken() throws IOException {
  // if( keycloakConfiguration != null ) {
  // KeycloakService.refreshToken(keycloakConfiguration, (BindingProvider) psnManager);
  // return KeycloakService.refreshToken(keycloakConfiguration, (BindingProvider) domainManager);
  // }
  // return null;
  // }

}
