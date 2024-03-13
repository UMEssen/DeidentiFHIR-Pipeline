package de.ume.deidentifhirpipeline.service;

import java.util.List;
import java.util.Map;

public interface PseudonymizationServiceInterface {

  public void createIfDomainIsNotExistent(String domain)  throws Exception;

  public void createIfDateShiftingDomainIsNotExistent(String domain, long millis) throws Exception;

  public Map<String, String> getOrCreatePseudonyms(List<String> ids, String domain) throws Exception;

  public long getDateShiftingValue(String id, String domain) throws Exception;

}
