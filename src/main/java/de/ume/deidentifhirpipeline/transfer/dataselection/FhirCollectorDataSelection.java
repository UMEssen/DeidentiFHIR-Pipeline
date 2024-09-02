package de.ume.deidentifhirpipeline.transfer.dataselection;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import de.ume.deidentifhirpipeline.configuration.ProjectConfiguration;
import de.ume.deidentifhirpipeline.configuration.dataselection.FhirCollectorDataSelectionConfiguration;
import de.ume.deidentifhirpipeline.configuration.dataselection.FhirServerDataSelectionConfiguration;
import de.ume.deidentifhirpipeline.transfer.Context;
import de.ume.deidentifhirpipeline.transfer.Utils;
import de.ume.deidentifhirpipeline.transfer.dataselection.fhircollector.FhirCollector;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.OptionalLong;

@Slf4j
public class FhirCollectorDataSelection extends DataSelection {

  @Override
  public void before(ProjectConfiguration projectConfiguration) throws Exception {
    // Nothing to do before processing
  }

  @Override
  public Context process(Context context) {
    try {
      FhirCollectorDataSelectionConfiguration configuration = context.getProjectConfiguration().getDataSelection().getFhirCollector();
      FhirCollector fhirCollector = new FhirCollector(configuration.getConfigurationFile());
      fhirCollector.fetchResources(context.getPatientId());
      log.debug(fhirCollector.toString());
      log.debug(Utils.fhirBundleToString(fhirCollector.getBundle()));
      context.setBundle(fhirCollector.getBundle());

      return context;
    } catch (Exception e) {
      return Utils.handleException(context, e);
    }
  }


}
