package de.ume.deidentifhirpipeline.mvh.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reference {
  private String id;
  private String type;
  private String display;

  public Reference(String id) {
    this.id = id;
  }

  public Reference(String id, String type) {
    this.id = id;
    this.type = type;
  }
}
