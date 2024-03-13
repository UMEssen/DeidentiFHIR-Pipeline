GET http://localhost:8042/healthcheck
[Options]
retry: 100
retry-interval: 1000
HTTP 200

# Post test bundle
POST http://localhost:8082/fhir
Content-Type: application/json
[Options]
retry: 50
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
retry: 10   # maximum number of retry, -1 for unlimited
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
# jsonpath "$.status" == "COMPLETED"
jsonpath "$.status" == "PARTIALLY_FAILED"

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

