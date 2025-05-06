package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Component("cohort-selection.via-file")
public class FileCohortSelection implements CohortSelection {
  @Override
  public List<String> before(ProjectConfig projectConfig) throws Exception {
    String path = projectConfig.getCohortSelection().getViaFile().getPath();
    String delimiter = projectConfig.getCohortSelection().getViaFile().getDelimiter();
    Path filePath = Paths.get(path);
    String content = Files.readString(filePath);
    return Arrays.stream(content.split(delimiter))
        .filter(s -> !s.isBlank())
        .map(s -> s.trim())
        .toList();
  }
}
