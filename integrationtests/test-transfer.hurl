# Wait till program started
GET http://localhost:8042/healthcheck
[Options]
retry: 100
retry-interval: 1000
HTTP 200

# Post test bundle
POST http://localhost:8082/fhir
Content-Type: application/json
[BasicAuth]
user: password
[Options]
retry: 50 # Could take some time till the fhir server started
file,testbundle1.json;
HTTP 200

# Start transfer
POST http://localhost:8042/start
{
    "project": "test-project1"
}
HTTP 200
[Captures]
transfer_id: jsonpath "$.transferId"

# Check if transfer is pending
GET http://localhost:8042/transfer/{{transfer_id}}
[Options]
retry: 100   # maximum number of retry, -1 for unlimited
retry-interval: 100
HTTP 200
[Asserts]
jsonpath "$.status" == "PENDING"

# Check if transfer is completed
GET http://localhost:8042/transfer/{{transfer_id}}
[Options]
retry: 100
retry-interval: 100
HTTP 200
[Asserts]
jsonpath "$.status" == "PARTIALLY_FAILED" # because only one bundle was available in the fhir server, the second not

# Check if Patient resource was transfered
GET http://localhost:8083/fhir/Patient
[Options]
retry: 10
HTTP 200
[Asserts]
jsonpath "$.total" == 1
jsonpath "$.entry[0].resource.id" != "1234"
jsonpath "$.entry[0].resource.name[0].family" == "PSEUDONYMISIERT"

# Check if Encounter resource was transfered
GET http://localhost:8083/fhir/Encounter
[Options]
retry: 10
HTTP 200
[Asserts]
jsonpath "$.total" == 1
jsonpath "$.entry[0].resource.id" != "12345"
jsonpath "$.entry[0].resource.subject.reference" != "Patient/1234"
jsonpath "$.entry[0].resource.diagnosis[0].condition.reference" != "Condition/123456"

# Check if Condition resource was transfered
GET http://localhost:8083/fhir/Condition
[Options]
retry: 10
HTTP 200
[Asserts]
jsonpath "$.total" == 2

# Check if Medication resource was transfered
GET http://localhost:8083/fhir/Medication
[Options]
retry: 10
HTTP 200
[Asserts]
jsonpath "$.total" == 1


#####################################################
### Start second transfer with another testbundle ###
#####################################################

# Wait till program started
GET http://localhost:8042/healthcheck
[Options]
retry: 100
retry-interval: 1000
HTTP 200

# Post test bundle2
POST http://localhost:8082/fhir
Content-Type: application/json
[BasicAuth]
user: password
[Options]
retry: 50
file,testbundle2.json;
HTTP 200

# Start transfer
POST http://localhost:8042/start
{
    "project": "test-project1"
}
HTTP 200
[Captures]
transfer_id: jsonpath "$.transferId"

# Check if transfer is pending
GET http://localhost:8042/transfer/{{transfer_id}}
[Options]
retry: 100   # maximum number of retry, -1 for unlimited
retry-interval: 100
HTTP 200
[Asserts]
jsonpath "$.status" == "PENDING"

# Check if transfer is completed
GET http://localhost:8042/transfer/{{transfer_id}}
[Options]
retry: 100
retry-interval: 100
HTTP 200
[Asserts]
jsonpath "$.status" == "COMPLETED" # Now completed with second bundle also transfered

# Check if Patient resource was transfered
GET http://localhost:8083/fhir/Patient
[Options]
retry: 10
HTTP 200
[Asserts]
jsonpath "$.total" == 2 # two Patients now

# Check if Condition resource was transfered
GET http://localhost:8083/fhir/Condition
[Options]
retry: 10
HTTP 200
[Asserts]
jsonpath "$.total" == 2

# Check if Medication resource was transfered
GET http://localhost:8083/fhir/Medication
[Options]
retry: 10
HTTP 200
[Asserts]
jsonpath "$.total" == 1 # Still only one Medication with the same id
