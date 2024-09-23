package de.ume.deidentifhirpipeline.mvh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Therapy {
  private List<History> history;
}
