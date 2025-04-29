package de.ume.deidentifhirpipeline.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

  @Value("${aop.method-logging:false}") private boolean enabled;

  @Before("execution(* de.ume.deidentifhirpipeline..*(..))")
  public void logBeforeMethod(JoinPoint joinPoint) {
    if (enabled) {
      System.out.println("Methode aufgerufen: " + joinPoint.getSignature());
      System.out.println("Parameter: " + String.join(", ", Arrays.stream(joinPoint.getArgs()).map(Object::toString).toList()));
    }
  }
}
