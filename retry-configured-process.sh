#!/bin/bash

curl --silent -S --location \
  --request POST 'http://localhost:8042/retry' \
  --header 'Content-Type: application/json' \
  --data '{
              "project": "test-project1",
              "startDateTime": "2025-08-29T14:58:26.124180589",
              "endDateTime": "2025-08-29T14:59:58.775651032",
              "status": "PARTIALLY_FAILED",
              "total": 2,
              "pending": 0,
              "completed": 1,
              "failed": 1,
              "errorMessagesWithCount": {
                "NullPointerException - Cannot invoke \"org.hl7.fhir.r4.model.Resource.getIdPart()\" because the return value of \"org.hl7.fhir.r4.model.Bundle$BundleEntryComponent.getResource()\" is null": 1
              },
              "map": {
                "1234-identifier": {
                  "status": "COMPLETED",
                  "endDate": "2025-08-29T14:59:58.773949474"
                },
                "234-identifier": {
                  "status": "FAILED",
                  "endDate": "2025-08-29T14:59:57.382158545",
                  "exception": {
                    "cause": null,
                    "stackTrace": [
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "getFhirId",
                        "fileName": "FhirServerDataSelection.java",
                        "lineNumber": 66,
                        "className": "de.ume.deidentifhirpipeline.transfer.dataselection.FhirServerDataSelection",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "process",
                        "fileName": "FhirServerDataSelection.java",
                        "lineNumber": 37,
                        "className": "de.ume.deidentifhirpipeline.transfer.dataselection.FhirServerDataSelection",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": "java.base",
                        "moduleVersion": "24.0.2",
                        "methodName": "invoke",
                        "fileName": null,
                        "lineNumber": -1,
                        "className": "jdk.internal.reflect.DirectMethodHandleAccessor",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": "java.base",
                        "moduleVersion": "24.0.2",
                        "methodName": "invoke",
                        "fileName": null,
                        "lineNumber": -1,
                        "className": "java.lang.reflect.Method",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "invokeJoinpointUsingReflection",
                        "fileName": "AopUtils.java",
                        "lineNumber": 360,
                        "className": "org.springframework.aop.support.AopUtils",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "invokeJoinpoint",
                        "fileName": "ReflectiveMethodInvocation.java",
                        "lineNumber": 196,
                        "className": "org.springframework.aop.framework.ReflectiveMethodInvocation",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "proceed",
                        "fileName": "ReflectiveMethodInvocation.java",
                        "lineNumber": 163,
                        "className": "org.springframework.aop.framework.ReflectiveMethodInvocation",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "invoke",
                        "fileName": "MethodBeforeAdviceInterceptor.java",
                        "lineNumber": 58,
                        "className": "org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "proceed",
                        "fileName": "ReflectiveMethodInvocation.java",
                        "lineNumber": 184,
                        "className": "org.springframework.aop.framework.ReflectiveMethodInvocation",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "invoke",
                        "fileName": "ExposeInvocationInterceptor.java",
                        "lineNumber": 97,
                        "className": "org.springframework.aop.interceptor.ExposeInvocationInterceptor",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "proceed",
                        "fileName": "ReflectiveMethodInvocation.java",
                        "lineNumber": 184,
                        "className": "org.springframework.aop.framework.ReflectiveMethodInvocation",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "intercept",
                        "fileName": "CglibAopProxy.java",
                        "lineNumber": 728,
                        "className": "org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "process",
                        "fileName": "<generated>",
                        "lineNumber": -1,
                        "className": "de.ume.deidentifhirpipeline.transfer.dataselection.FhirServerDataSelection$$SpringCGLIB$$0",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "executeDataSelection",
                        "fileName": "TransferProcess.java",
                        "lineNumber": 215,
                        "className": "de.ume.deidentifhirpipeline.transfer.TransferProcess",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "executeDataSelectionWithSemaphore",
                        "fileName": "TransferProcess.java",
                        "lineNumber": 201,
                        "className": "de.ume.deidentifhirpipeline.transfer.TransferProcess",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "lambda$processWithVirtualThreads$0",
                        "fileName": "TransferProcess.java",
                        "lineNumber": 149,
                        "className": "de.ume.deidentifhirpipeline.transfer.TransferProcess",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": "java.base",
                        "moduleVersion": "24.0.2",
                        "methodName": "call",
                        "fileName": null,
                        "lineNumber": -1,
                        "className": "java.util.concurrent.Executors$RunnableAdapter",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": "java.base",
                        "moduleVersion": "24.0.2",
                        "methodName": "run",
                        "fileName": null,
                        "lineNumber": -1,
                        "className": "java.util.concurrent.FutureTask",
                        "nativeMethod": false
                      },
                      {
                        "classLoaderName": null,
                        "moduleName": "java.base",
                        "moduleVersion": "24.0.2",
                        "methodName": "run",
                        "fileName": null,
                        "lineNumber": -1,
                        "className": "java.lang.VirtualThread",
                        "nativeMethod": false
                      }
                    ],
                    "message": "Cannot invoke \"org.hl7.fhir.r4.model.Resource.getIdPart()\" because the return value of \"org.hl7.fhir.r4.model.Bundle$BundleEntryComponent.getResource()\" is null",
                    "suppressed": [],
                    "localizedMessage": "Cannot invoke \"org.hl7.fhir.r4.model.Resource.getIdPart()\" because the return value of \"org.hl7.fhir.r4.model.Bundle$BundleEntryComponent.getResource()\" is null"
                  }
                }
              }
            }'

