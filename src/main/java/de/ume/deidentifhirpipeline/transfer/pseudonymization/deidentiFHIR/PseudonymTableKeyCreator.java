package de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentiFHIR;

public interface PseudonymTableKeyCreator {

  String getKeyForResourceTypeAndID(String resourceType, String id);

  String getKeyForSystemAndValue(String system, String value);
}
