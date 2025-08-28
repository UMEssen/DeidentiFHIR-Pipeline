package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.cohortselection.FiremetricsCohortSelectionConfig;
import de.ume.deidentifhirpipeline.transfer.Cohort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("cohort-selection.firemetrics")
public class FiremetricsCohortSelection implements CohortSelection {

  @Override
  public Cohort before(ProjectConfig projectConfig) throws Exception {
    FiremetricsCohortSelectionConfig config = projectConfig.getCohortSelection().getFiremetrics();

    // read fhirql statement from variable if provided or from file
    String fhirqlStatement = config.getQuery();
    if (fhirqlStatement == null)
      fhirqlStatement = Files.readString(Path.of(config.getQueryFile()));


    // execute fhirql query
    Class.forName("org.postgresql.Driver");

    String jdbcConnectionUrl = String.format(
        "jdbc:postgresql://%s:%s/%s",
        config.getHost(), config.getPort(), config.getDatabase());

    List<String> patientList = null;
    try (Connection connection = DriverManager.getConnection(
        jdbcConnectionUrl,
        config.getUser(),
        config.getPassword())) {

      log.debug("Connected to FHIRQL database!");
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(fhirqlStatement);

      patientList = new ArrayList<>();
      while (resultSet.next()) {
        patientList.add(resultSet.getString("id"));
      }

    } catch (SQLException e) {
      log.error("FHIRQL database connection failure.");
      e.printStackTrace();
    }

    return new Cohort(patientList, null);
  }

}
