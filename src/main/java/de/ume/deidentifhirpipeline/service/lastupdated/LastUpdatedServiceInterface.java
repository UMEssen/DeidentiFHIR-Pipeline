package de.ume.deidentifhirpipeline.service.lastupdated;

public interface LastUpdatedServiceInterface {

  void createIfLastUpdatedDomainIsNotExistent() throws Exception;
  long getLastUpdatedValue(String id) throws Exception;
  void setLastUpdatedValue(String id, long lastUpdated) throws Exception;
}
