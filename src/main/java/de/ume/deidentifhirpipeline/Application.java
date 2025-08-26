package de.ume.deidentifhirpipeline;

import de.ume.deidentifhirpipeline.transfer.dataselection.fhircollector.FhirCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;


@Slf4j
@EnableAutoConfiguration
@EnableRetry
@EnableScheduling
@SpringBootApplication
public class Application {

  public static void main(String[] args) throws IOException {
    SpringApplication.run(Application.class, args);

    var fhirCollector = new FhirCollector();
    fhirCollector.loadRuntimeCachingProvider();

  }

}
