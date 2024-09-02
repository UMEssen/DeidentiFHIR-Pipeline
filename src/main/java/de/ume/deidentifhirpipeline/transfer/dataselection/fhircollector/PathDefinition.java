package de.ume.deidentifhirpipeline.transfer.dataselection.fhircollector;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PathDefinition {
  public List<String> queries;
  public List<String> fhirpaths;
}
