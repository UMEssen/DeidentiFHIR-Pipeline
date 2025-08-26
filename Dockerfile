FROM eclipse-temurin:24.0.2_12-jre AS deidentifhir-pipeline
ARG deidentifhir_pipeline_version
COPY ./target/deidentifhir-pipeline-${deidentifhir_pipeline_version}-runnable.jar /deidentifhir-pipeline.jar
COPY ./src/main/resources/application.yaml /conf/application.yaml
COPY ./deidentifhir /deidentifhir
EXPOSE 8042
ENTRYPOINT ["java","-Dloader.path=/plugin/","-jar","/deidentifhir-pipeline.jar","--spring.config.location=/conf/application.yaml"]
