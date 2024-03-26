package de.ume.deidentifhirpipeline.service.pseudonymization;

import java.util.List;
import java.util.Map;

public interface PseudonymizationServiceInterface {

  void createIfDomainIsNotExistent()  throws Exception;

  void createIfDateShiftingDomainIsNotExistent(long millis) throws Exception;

  Map<String, String> getOrCreatePseudonyms(List<String> ids) throws Exception;

  long getDateShiftingValue(String id) throws Exception;

}
