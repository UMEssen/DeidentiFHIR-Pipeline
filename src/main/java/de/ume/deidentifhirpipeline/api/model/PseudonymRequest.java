package de.ume.deidentifhirpipeline.api.model;

import lombok.Getter;

import java.util.List;

public class PseudonymRequest {
  @Getter
  String domain;
  @Getter
  List<String> ids;
}
