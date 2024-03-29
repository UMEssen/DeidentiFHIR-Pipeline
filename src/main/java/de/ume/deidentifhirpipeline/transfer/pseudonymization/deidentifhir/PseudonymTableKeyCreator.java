package de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentifhir;

public interface PseudonymTableKeyCreator {

  String getKeyForResourceTypeAndID(String resourceType, String id);

  String getKeyForSystemAndValue(String system, String value);
}
