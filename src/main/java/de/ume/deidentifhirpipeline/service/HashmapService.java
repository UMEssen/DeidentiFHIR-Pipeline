package de.ume.deidentifhirpipeline.service;

import com.sun.xml.ws.fault.ServerSOAPFaultException;
import de.ume.deidentifhirpipeline.api.data.TransferStatus;
import de.ume.deidentifhirpipeline.configuration.auth.KeycloakAuthConfiguration;
import de.ume.deidentifhirpipeline.configuration.service.GpasServiceConfiguration;
import de.ume.deidentifhirpipeline.configuration.service.HashmapServiceConfiguration;
import de.ume.deidentifhirpipeline.service.exception.AddDomainException;
import de.ume.deidentifhirpipeline.service.exception.GetDomainException;
import de.ume.deidentifhirpipeline.service.exception.InsertValuePseudonymPairException;
import de.ume.deidentifhirpipeline.service.exception.TokenException;
import de.ume.deidentifhirpipeline.service.gpas.SoapTemplates;
import de.ume.deidentifhirpipeline.service.wsdl.gpas.*;
import de.ume.deidentifhirpipeline.service.wsdl.gpasDomain.DomainManager;
import de.ume.deidentifhirpipeline.service.wsdl.gpasDomain.DomainManagerBeanService;
import de.ume.deidentifhirpipeline.service.wsdl.gpasDomain.ListDomainsResponse;
import de.ume.deidentifhirpipeline.transfer.Utils;
import jakarta.xml.ws.BindingProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class HashmapService implements PseudonymizationServiceInterface {

  private final HashmapServiceConfiguration configuration;

  private static final ConcurrentMap<String, ConcurrentMap<String, String>> domainMap = new ConcurrentHashMap<>();

  public HashmapService(HashmapServiceConfiguration configuration) {
    this.configuration = configuration;
  }

  public void createIfDomainIsNotExistent(String domain) {
    if( !domainMap.containsKey(domain) ) {
      domainMap.put(domain, new ConcurrentHashMap<>());
      log.info("Domain created: " + domain);
    }
  }

  public void createIfDateShiftingDomainIsNotExistent(String domain, long millis) {
    String dateShiftingDomain = domain + Utils.DATE_SHIFTING_DOMAIN_SUFFIX;
    if( !domainMap.containsKey(dateShiftingDomain) ) {
      ConcurrentMap<String,String> pseudonymMap = new ConcurrentHashMap<>();
      pseudonymMap.put(Utils.DATE_SHIFTING_DOMAIN_VALUE, String.valueOf(millis));
      domainMap.put(dateShiftingDomain, pseudonymMap);
      log.info("Domain created: " + dateShiftingDomain);
    }
  }

  public Map<String, String> getOrCreatePseudonyms(List<String> ids, String domain) {
    ConcurrentMap<String, String> pseudonymMap = domainMap.get(domain);
    Map<String, String> mapToBeReturned = new HashMap<>();
    for( String id : ids ) {
      if( !pseudonymMap.containsKey(id) ) {
        String value = String.valueOf(UUID.randomUUID());
        mapToBeReturned.put(id, value);
        pseudonymMap.put(id, value);
      }
      else mapToBeReturned.put(id, pseudonymMap.get(id));
    }
    return mapToBeReturned;
  }

  public long getDateShiftingValue(String id, String domain) throws Exception {
    Map<String, String> pseudonymMap = domainMap.get(domain + Utils.DATE_SHIFTING_DOMAIN_SUFFIX);
    if( pseudonymMap.containsKey(id) ) return Long.parseLong(pseudonymMap.get(id));
    else {
      long millis = Long.parseLong(pseudonymMap.get(Utils.DATE_SHIFTING_DOMAIN_VALUE));
      long range = new RandomDataGenerator().nextLong(0, millis);
      long result = range - (range / 2);
      pseudonymMap.put(id, String.valueOf(result));
      return result;
    }
  }

}
