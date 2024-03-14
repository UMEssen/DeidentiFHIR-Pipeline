package de.ume.deidentifhirpipeline.service;

import de.ume.deidentifhirpipeline.configuration.service.HashmapServiceConfiguration;
import de.ume.deidentifhirpipeline.transfer.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class HashmapService implements PseudonymizationServiceInterface {

  private final HashmapServiceConfiguration configuration;
  private final String domain;
  private final String dateShiftingDomain;

  private static final ConcurrentMap<String, ConcurrentMap<String, String>> domainMap = new ConcurrentHashMap<>();

  public HashmapService(HashmapServiceConfiguration configuration) {
    this.configuration = configuration;
    this.domain = configuration.getDomain();
    this.dateShiftingDomain = Utils.getDateShiftingDomainName(this.domain);
  }

  public void createIfDomainIsNotExistent() {
    if( !domainMap.containsKey(this.domain) ) {
      domainMap.put(this.domain, new ConcurrentHashMap<>());
      log.info("Domain created: " + this.domain);
    }
  }

  public void createIfDateShiftingDomainIsNotExistent(long millis) {
    if( !domainMap.containsKey(this.dateShiftingDomain) ) {
      ConcurrentMap<String,String> pseudonymMap = new ConcurrentHashMap<>();
      pseudonymMap.put(Utils.DATE_SHIFTING_DOMAIN_VALUE, String.valueOf(millis));
      domainMap.put(this.dateShiftingDomain, pseudonymMap);
      log.info("Domain created: " + this.dateShiftingDomain);
    }
  }

  public Map<String, String> getOrCreatePseudonyms(List<String> ids) {
    ConcurrentMap<String, String> pseudonymMap = domainMap.get(this.domain);
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

  public long getDateShiftingValue(String id) throws Exception {
    Map<String, String> pseudonymMap = domainMap.get(this.dateShiftingDomain);
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
