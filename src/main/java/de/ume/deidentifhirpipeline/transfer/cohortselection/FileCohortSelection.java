package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component("cohort-selection.via-file")
public class FileCohortSelection implements CohortSelectionInterface {
  @Override
  public List<String> before(ProjectConfig projectConfig) throws Exception {
    String path = projectConfig.getCohortSelection().getViaFile().getPath();
    Path filePath = Paths.get(path);
    return Files.readAllLines(filePath).stream().filter(s -> !s.isBlank()).toList();
  }
}
