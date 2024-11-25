package de.ume.deidentifhirpipeline.service.pseudonymization;

import de.ume.deidentifhirpipeline.configuration.service.HashmapServiceConfiguration;
import de.ume.deidentifhirpipeline.service.lastupdated.LastUpdatedServiceInterface;
import de.ume.deidentifhirpipeline.transfer.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class HashmapService implements PseudonymizationServiceInterface, LastUpdatedServiceInterface {

  private final HashmapServiceConfiguration configuration;
  private final String domain;
  private final String dateShiftingDomain;
  private final String lastUpdatedDomain;

  public static final ConcurrentMap<String, ConcurrentMap<String, String>> domainMap = new ConcurrentHashMap<>();

  public HashmapService(HashmapServiceConfiguration configuration) {
    this.configuration = configuration;
    this.domain = configuration.getDomain();
    this.dateShiftingDomain = Utils.getDateShiftingDomainName(this.domain);
    this.lastUpdatedDomain = Utils.getLastUpdatedDomainName(this.domain);
  }

  @Override
  public void createIfDomainIsNotExistent() {
    if (!domainMap.containsKey(this.domain)) {
      domainMap.put(this.domain, new ConcurrentHashMap<>());
      log.info("Domain created: " + this.domain);
    }
  }

  @Override
  public void createIfDateShiftingDomainIsNotExistent(long millis) {
    if (!domainMap.containsKey(this.dateShiftingDomain)) {
      ConcurrentMap<String, String> pseudonymMap = new ConcurrentHashMap<>();
      pseudonymMap.put(Utils.DATE_SHIFTING_DOMAIN_VALUE, String.valueOf(millis));
      domainMap.put(this.dateShiftingDomain, pseudonymMap);
      log.info("Domain created: " + this.dateShiftingDomain);
    }
  }

  @Override
  public void createIfLastUpdatedDomainIsNotExistent() {
    if (!domainMap.containsKey(this.lastUpdatedDomain)) {
      ConcurrentMap<String, String> pseudonymMap = new ConcurrentHashMap<>();
      domainMap.put(this.lastUpdatedDomain, pseudonymMap);
      log.info("Domain created: " + this.lastUpdatedDomain);
    }
  }

  @Override
  public Map<String, String> getOrCreatePseudonyms(List<String> ids) {
    ConcurrentMap<String, String> pseudonymMap = domainMap.get(this.domain);
    Map<String, String> mapToBeReturned = new HashMap<>();
    for (String id : ids) {
      if (!pseudonymMap.containsKey(id)) {
        String value = String.valueOf(UUID.randomUUID());
        mapToBeReturned.put(id, value);
        pseudonymMap.put(id, value);
      } else
        mapToBeReturned.put(id, pseudonymMap.get(id));
    }
    return mapToBeReturned;
  }

  @Override
  public long getDateShiftingValue(String id) {
    Map<String, String> pseudonymMap = domainMap.get(this.dateShiftingDomain);
    if (pseudonymMap.containsKey(id))
      return Long.parseLong(pseudonymMap.get(id));
    else {
      long millis = Long.parseLong(pseudonymMap.get(Utils.DATE_SHIFTING_DOMAIN_VALUE));
      long range = new RandomDataGenerator().nextLong(0, millis);
      long result = range - (range / 2);
      pseudonymMap.put(id, String.valueOf(result));
      return result;
    }
  }

  @Override
  public long getLastUpdatedValue(String id) {
    Map<String, String> pseudonymMap = domainMap.get(this.lastUpdatedDomain);
    if (pseudonymMap.containsKey(id))
      return Long.parseLong(pseudonymMap.get(id));
    else {
      ZonedDateTime zdt = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
      long lastUpdated = zdt.toInstant().toEpochMilli();
      pseudonymMap.put(id, String.valueOf(lastUpdated));
      return lastUpdated;
    }
  }

  @Override
  public void setLastUpdatedValue(String id, long lastUpdated) {
    Map<String, String> pseudonymMap = domainMap.get(this.lastUpdatedDomain);
    pseudonymMap.put(id, String.valueOf(lastUpdated));
  }

}
