package de.ume.deidentifhirpipeline;

import de.ume.deidentifhirpipeline.transfer.dataselection.fhircollector.FhirCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAutoConfiguration
@Slf4j
public class Application {

  public static void main(String[] args) throws IOException {
    SpringApplication.run(Application.class, args);

    var fhirCollector = new FhirCollector();
    fhirCollector.loadRuntimeCachingProvider();

  }

}
