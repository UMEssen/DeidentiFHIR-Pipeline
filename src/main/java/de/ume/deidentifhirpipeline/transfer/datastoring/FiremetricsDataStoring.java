package de.ume.deidentifhirpipeline.transfer.datastoring;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.datastoring.FiremetricsDataStoringConfig;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

@Slf4j
@Component("data-storing.firemetrics")
public class FiremetricsDataStoring implements DataStoring {

  public void before(ProjectConfig projectConfig) throws Exception {
    // Nothing to do before processing
  }

  public Context process(Context context) {
    FiremetricsDataStoringConfig config = context.getProjectConfig().getDataStoring().getFiremetrics();
    try {
      List<String> resourcesAsString = context.getBundle().getEntry().stream().map(e -> Utils.fhirResourceToString(e.getResource())).toList();

      // log bundle
      if (config.isWriteBundlesToFiles()) {
        String bundleAsString = Utils.fhirResourceToString(context.getBundle());
        Path path = Paths.get("./bundles/", context.getPatientId() + ".json");
        Files.write(path, bundleAsString.getBytes(StandardCharsets.UTF_8));
      }

      // execute fhirql query
      Class.forName("org.postgresql.Driver");

      String jdbcConnectionUrl = String.format(
          "jdbc:postgresql://%s:%s/%s",
          config.getHost(), config.getPort(), config.getDatabase());

      try (Connection connection = DriverManager.getConnection(
          jdbcConnectionUrl,
          config.getUser(),
          config.getPassword())) {

        log.debug("Connected to FHIRQL database!");

        for (String resourceAsString : resourcesAsString) {
          String fhirqlStatement = String.format("select public.fql_insert('%s'::jsonb);", resourceAsString);
          try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(fhirqlStatement);
          } catch (SQLException e) {
            log.error("Could not insert: ");
            log.error(resourceAsString);
            e.printStackTrace();
          }
        }
      } catch (SQLException e) {
        log.error("FHIRQL database connection failure.");
        e.printStackTrace();
      }
      return context;
    } catch (Exception e) {
      e.printStackTrace();
      return Utils.handleException(context, e);
    }
  }

}
