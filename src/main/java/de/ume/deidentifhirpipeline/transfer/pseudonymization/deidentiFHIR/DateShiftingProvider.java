package de.ume.deidentifhirpipeline.transfer.pseudonymization.deidentiFHIR;

import de.ume.deidentifhir.util.ShiftDateProvider;

import java.util.Map;

public class DateShiftingProvider implements ShiftDateProvider {

  private Map<String, Long> timeShiftMap;

  public void setTimeShiftMap(Map<String, Long> timeShiftMap) {
    this.timeShiftMap = timeShiftMap;
  }

  @Override
  public Long getDateShiftingValueInMillis(String key) {

    // TODO check if timeShiftMap contains a mapping for this id, throw an exception otherwise!
    // throwing a RuntimeException for now
    if (!timeShiftMap.containsKey(key)) {
      throw new RuntimeException("no valid mapping found for id: " + key);
    }

    return timeShiftMap.get(key);
  }
}
