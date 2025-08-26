package de.ume.deidentifhirpipeline.plugin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RandomService {
  @Retryable(maxAttempts = 3)
  public void bla() throws Exception {
    log.info("bla called.");
    throw new Exception("exception");
  }

  @Scheduled(fixedRate = 5000)
  public void test() {
    log.info("test() ...");
  }
}
