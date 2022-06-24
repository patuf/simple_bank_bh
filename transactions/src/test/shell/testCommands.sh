curl -X GET -H 'Content-Type: application/json' 'http://localhost:8081/rest/v1.0/report/balancesForAccounts?entityIds=1,2,3' | jq .
curl -X GET -H 'Content-Type: application/json' 'http://localhost:8081/rest/v1.0/report/balancesForCustomers?entityIds=1,2,3'
curl -X GET -H 'Content-Type: application/json' 'http://localhost:8081/rest/v1.0/report/transactionsForAccount?accountId=1' | jq .

curl -X POST -H 'Content-Type: application/json' 'http://localhost:8081/rest/v1.0/transaction' -d '{"customerId": 1, "accountId": 1, "amount": 200., "timeCreated": "2022-06-23T21:38:11.494796"}'