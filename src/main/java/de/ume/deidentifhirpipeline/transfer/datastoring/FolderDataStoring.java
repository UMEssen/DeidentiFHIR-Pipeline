package de.ume.deidentifhirpipeline.transfer.datastoring;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.datastoring.FhirServerDataStoringConfiguration;
import de.ume.deidentifhirpipeline.configuration.datastoring.FolderDataStoringConfiguration;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import org.hl7.fhir.r4.model.Bundle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class FolderDataStoring extends DataStoring {

  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    // Nothing to do before processing
  }

  public Context process(Context context) {
    FolderDataStoringConfiguration configuration = context.getProjectConfiguration().getDataStoring().getFolder();
    try {
      // Create directories
      Files.createDirectories(Paths.get(configuration.getPath()));

      // Set filename to patient fhir id, else random UUID
      String filename = context.getBundle().getEntry().stream()
          .filter(b -> b.getResource().fhirType().equals("Patient"))
          .map(b -> b.getResource().getIdPart())
          .findFirst()
          .orElse(UUID.randomUUID().toString());

      // Create bundle file
      String pathAndFilename = configuration.getPath() + filename + ".json";
      Path file = Paths.get(pathAndFilename);
      String bundleAsString = Utils.fhirBundleToStringPrettyPrint(context.getBundle());
      if (Files.exists(file))
        Files.writeString(file, bundleAsString, StandardOpenOption.TRUNCATE_EXISTING);
      else
        Files.writeString(file, bundleAsString, StandardOpenOption.CREATE);

      return context;
    } catch (Exception e) {
      return Utils.handleException(context, e);
    }
  }

}
