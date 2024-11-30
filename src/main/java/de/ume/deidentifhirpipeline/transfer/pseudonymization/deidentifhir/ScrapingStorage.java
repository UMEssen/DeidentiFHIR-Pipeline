package de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentifhir;

import de.ume.deidentifhir.util.IDReplacementProvider;
import de.ume.deidentifhir.util.IdentifierValueReplacementProvider;

import java.util.HashSet;
import java.util.Set;

public class ScrapingStorage implements IDReplacementProvider, IdentifierValueReplacementProvider {
  private PseudonymTableKeyCreator keyCreator;

  public HashSet<String> gatheredIDATs = new HashSet<>();

  public ScrapingStorage() {}

  public void setPseudonymTableKeyCreator(PseudonymTableKeyCreator keyCreator) {
    this.keyCreator = keyCreator;
  }

  @Override
  public String getIDReplacement(String resourceType, String id) {
    gatheredIDATs.add(keyCreator.getKeyForResourceTypeAndID(resourceType, id));
    return id;
  }

  @Override
  public String getValueReplacement(String system, String value) {
    gatheredIDATs.add(keyCreator.getKeyForSystemAndValue(system, value));
    return value;
  }

  public Set getAndResetGatheredIDATs() {
    Set cloned = (Set) gatheredIDATs.clone();
    gatheredIDATs.clear();
    return cloned;
  }
}
