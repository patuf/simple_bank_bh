curl -X POST -H 'Content-Type: application/json' http://localhost:8080/rest/v1.0/account -d '{"customerId": 1, "initialCredit": 1}'
curl -X GET -H 'Content-Type: application/json' 'http://localhost:8080/rest/v1.0/account/1' | jq .

curl -X GET -H 'Content-Type: application/json' 'http://localhost:8080/rest/v1.0/report/customers?page=1&size=2' | jq .
curl -X GET -H 'Content-Type: application/json' 'http://localhost:8080/rest/v1.0/report/customerAccounts/0?page=1&size=2' | jq .