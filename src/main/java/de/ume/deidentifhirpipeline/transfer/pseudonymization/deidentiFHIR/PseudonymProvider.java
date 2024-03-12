package de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentiFHIR;

import de.ume.deidentifhir.util.IDReplacementProvider;
import de.ume.deidentifhir.util.IdentifierValueReplacementProvider;

import java.util.Map;

public class PseudonymProvider
    implements IDReplacementProvider, IdentifierValueReplacementProvider {

  private PseudonymTableKeyCreator keyCreator;

  public PseudonymProvider() {}

  public PseudonymProvider(PseudonymTableKeyCreator keyCreator) {
    this.keyCreator = keyCreator;
  }

  private Map<String, String> pseudonymMap;

  public void setPseudonymTableKeyCreator(PseudonymTableKeyCreator keyCreator) {
    this.keyCreator = keyCreator;
  }

  public void setPseudonymMap(Map<String, String> pseudonymMap) {
    this.pseudonymMap = pseudonymMap;
  }

  @Override
  public String getIDReplacement(String resourceType, String id) {
    String key = keyCreator.getKeyForResourceTypeAndID(resourceType, id);

    // TODO check if pseudonymMap contains a mapping for this id, throw an exception otherwise!
    // throwing a RuntimeException for now
    if (!pseudonymMap.containsKey(key)) {
      throw new RuntimeException("no valid mapping found for id: " + key);
    }

    return pseudonymMap.get(key);
  }

  @Override
  public String getValueReplacement(String system, String value) {
    String key = keyCreator.getKeyForSystemAndValue(system, value);

    // TODO check if pseudonymMap contains a mapping for this id, throw an exception otherwise!
    // throwing a RuntimeException for now
    if (!pseudonymMap.containsKey(key)) {
      throw new RuntimeException("no valid mapping found for value: " + key);
    }

    return pseudonymMap.get(key);
  }
}
