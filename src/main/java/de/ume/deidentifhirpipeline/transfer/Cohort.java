package de.ume.deidentifhirpipeline.transfer;

import java.util.List;
import java.util.Map;

public record Cohort(List<String> ids, Map<String, String> filteredOutIdsWithErrorMessages) {
}
