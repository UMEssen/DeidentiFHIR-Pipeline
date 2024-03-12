package de.ume.deidentifhirpipeline.transfer.dataselection;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.dataselection.FhirServerDataSelectionConfiguration;
import de.ume.deidentifhirpipeline.configuration.dataselection.FiremetricsDataSelectionConfiguration;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

@Slf4j
public class FiremetricsDataSelection extends DataSelection {

  private final FiremetricsDataSelectionConfiguration config;

  public FiremetricsDataSelection(FiremetricsDataSelectionConfiguration config) {
    this.config = config;
  }

  @Override
  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    // Nothing to do before processing
  }

  @Override
  public Context process(Context context) throws Exception {


    // load fhirql statement from variable if existent or from file
//    String fhirqlStatementWithReplacementString = (String) execution.getVariable("data-selection-via-fhirql");
    String fhirqlStatementWithReplacementString = config.getQuery();
    String replacementString = config.getQueryIdPlaceholderString();

//    if( fhirqlStatementWithReplacementString == null && config.getQueryFile() != null)
//      fhirqlStatementWithReplacementString = Files.readString(Path.of(config.getQueryFile()));
//    if( fhirqlStatementWithReplacementString == null ) fhirqlStatementWithReplacementString = config.getQuery();
//    if( replacementString == null ) replacementString = config.getQueryIdPlaceholderString();

    // replace id placeholder with the actual id
    String fhirqlStatement = fhirqlStatementWithReplacementString.replace(replacementString, context.getPatientId());
    log.debug(fhirqlStatement);


    // execute fhirql query
    Class.forName("org.postgresql.Driver");

    String jdbcConnectionUrl = String.format(
        "jdbc:postgresql://%s:%s/%s",
        config.getHost(), config.getPort(), config.getDatabase()
    );

    Bundle returnBundle = new Bundle();
    try (Connection connection = DriverManager.getConnection(
        jdbcConnectionUrl,
        config.getUser(),
        config.getPassword())) {

      log.debug("Connected to FHIRQL database!");
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(fhirqlStatement);

      while (resultSet.next()) {
        IBaseResource resource = Utils.fctx.newJsonParser().parseResource(resultSet.getString("_json"));
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
