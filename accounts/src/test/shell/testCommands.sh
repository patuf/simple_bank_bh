curl -X POST -H 'Content-Type: application/json' localhost:8080/rest/v1.0/account -d '{"customerId": 1, "initialCredit": 1}'

curl -X GET -H 'Content-Type: application/json' 'localhost:8080/rest/v1.0/account/byCustomer/0?page=0&size=4'
curl -X GET -H 'Content-Type: application/json' 'localhost:8080/rest/v1.0/report/customers?page=1&size=2' | jq .