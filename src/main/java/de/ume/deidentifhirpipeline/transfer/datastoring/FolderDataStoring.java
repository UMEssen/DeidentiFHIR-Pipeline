package de.ume.deidentifhirpipeline.transfer.datastoring;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.datastoring.FolderDataStoringConfig;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Component("data-storing.folder")
public class FolderDataStoring implements DataStoring {

  public void before(ProjectConfig projectConfig) throws Exception {
    // Nothing to do before processing
  }

  public void process(Context context) {
    FolderDataStoringConfig config = context.getProjectConfig().getDataStoring().getFolder();
    try {
      // Create directories
      Files.createDirectories(Paths.get(config.getPath()));

      // Set filename to patient fhir id, else random UUID
      String filename = context.getBundle().getEntry().stream()
          .filter(b -> b.getResource().fhirType().equals("Patient"))
          .map(b -> b.getResource().getIdPart())
          .findFirst()
          .orElse(UUID.randomUUID().toString());

      // Create bundle file
      String pathAndFilename = config.getPath() + filename + ".json";
      Path file = Paths.get(pathAndFilename);
      String bundleAsString = Utils.fhirBundleToStringPrettyPrint(context.getBundle());
      if (Files.exists(file))
        Files.writeString(file, bundleAsString, StandardOpenOption.TRUNCATE_EXISTING);
      else
        Files.writeString(file, bundleAsString, StandardOpenOption.CREATE);

    } catch (Exception e) {
      Utils.handleException(context, e);
    }
  }

}
