package de.ume.deidentifhirpipeline.transfer.dataselection.fhircollector;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FhirCollectorConfig {
  public String url;
//  private Map<String, List<Map<String,List<String>>>> resources;
  private List<Map<String, List<Map<String,List<String>>>>> resources;
//  public Map<String, PathDefinition> resources;

//  public String toString() {
//    return resources.toString();
//  }
}
