package de.ume.deidentifhirpipeline.config.cohortselection;

import de.ume.deidentifhirpipeline.config.plugin.PluginConfig;
import de.ume.deidentifhirpipeline.config.service.GicsServiceConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CohortSelectionConfig {
  private GicsServiceConfig                gics;
  private FiremetricsCohortSelectionConfig firemetrics;
  private IdCohortSelectionConfig          viaIds;
  private FileCohortSelectionConfig        viaFile;
  private CsvCohortSelectionConfig         viaCsv;
  private PluginConfig                     viaPlugin;
}
