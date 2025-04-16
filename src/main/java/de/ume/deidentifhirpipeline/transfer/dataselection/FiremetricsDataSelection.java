package de.ume.deidentifhirpipeline.transfer.dataselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import de.ume.deidentifhirpipeline.config.dataselection.FiremetricsDataSelectionConfig;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.OptionalLong;

@Slf4j
@Component("data-selection.firemetrics")
public class FiremetricsDataSelection implements DataSelection {

  @Override
  public void before(ProjectConfig projectConfig) throws Exception {
    // Nothing to do before processing
  }

  @Override
  public Context process(Context context) throws Exception {
    FiremetricsDataSelectionConfig config = context.getProjectConfig().getDataSelection().getFiremetrics();

    // load fhirql statement from variable if existent or from file
    String fhirqlStatementWithReplacementString = config.getQuery();
    String queryIdPlaceholderString = config.getQueryIdPlaceholderString();
    String queryLastUpdatedPlaceholderString = config.getQueryLastUpdatedPlaceholderString();

    // if( fhirqlStatementWithReplacementString == null && config.getQueryFile() != null)
    // fhirqlStatementWithReplacementString = Files.readString(Path.of(config.getQueryFile()));
    // if( fhirqlStatementWithReplacementString == null ) fhirqlStatementWithReplacementString =
    // config.getQuery();
    // if( queryIdPlaceholderString == null ) queryIdPlaceholderString =
    // config.getQueryIdPlaceholderString();

    // replace id placeholder with the actual id
    String fhirqlStatement = fhirqlStatementWithReplacementString.replace(queryIdPlaceholderString, context.getPatientId());

    // replace lastUpdated placeholder with the actual date if present
    if (context.getOldLastUpdated().isPresent()) {
      String dateString = Utils.longToFiremetricsDateString(context.getOldLastUpdated().getAsLong(), ZoneId.of("UTC"));
      fhirqlStatement = fhirqlStatement.replace(queryLastUpdatedPlaceholderString, dateString);
    }

    log.debug(fhirqlStatement);


    // execute fhirql query
    Class.forName("org.postgresql.Driver");

    String jdbcConnectionUrl = String.format(
        "jdbc:postgresql://%s:%s/%s",
        config.getHost(), config.getPort(), config.getDatabase());

    Bundle returnBundle = new Bundle();
    try (Connection connection = DriverManager.getConnection(
        jdbcConnectionUrl,
        config.getUser(),
        config.getPassword())) {
      if (context.getOldLastUpdated().isPresent()) {
        context.setNewLastUpdated(OptionalLong.of(ZonedDateTime.now().toInstant().toEpochMilli()));
      }
      log.debug("Connected to FHIRQL database!");
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(fhirqlStatement);

      while (resultSet.next()) {
        String result = resultSet.getString("_json");
        log.debug(result);
        IBaseResource resource = Utils.fctx.newJsonParser().parseResource(result);
        returnBundle.addEntry(new Bundle.BundleEntryComponent().setResource((Resource) resource));
      }
      context.setBundle(returnBundle);

    } catch (SQLException e) {
      log.error("FHIRQL database connection failure.");
      e.printStackTrace();
    }
    return context;
  }
}
