FROM eclipse-temurin:23.0.1_11-jre AS deidentifhir-pipeline
ARG deidentifhir_pipeline_version
COPY ./target/deidentifhir-pipeline-$deidentifhir_pipeline_version.jar /deidentifhir-pipeline.jar
COPY ./src/main/resources/application.yaml /conf/application.yaml
COPY ./deidentifhir /deidentifhir
EXPOSE 8042
ENTRYPOINT ["java","-jar","/deidentifhir-pipeline.jar","--spring.config.location=/conf/application.yaml"]
