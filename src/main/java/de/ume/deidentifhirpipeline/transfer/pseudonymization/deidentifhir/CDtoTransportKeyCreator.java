package de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentifhir;

public class CDtoTransportKeyCreator implements PseudonymTableKeyCreator {

  private String patientPrefix;

  public CDtoTransportKeyCreator(String patientPrefix) {
    this.patientPrefix = patientPrefix;
  }

  @Override
  public String getKeyForResourceTypeAndID(String resourceType, String id) {
    // TODO add null checks!
    return this.patientPrefix + ".id." + resourceType + "." + id;
  }

  @Override
  public String getKeyForSystemAndValue(String system, String value) {
    // TODO add null checks!
    return this.patientPrefix + ".identifier." + system + ":" + value;
  }
}
