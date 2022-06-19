curl -X GET -H 'Content-Type: application/json' 'http://localhost:8081/rest/v1.0/report/balancesForAccounts?accountIds=1,2,3' | jq .
curl -X GET -H 'Content-Type: application/json' 'http://localhost:8081/rest/v1.0/report/totalBalanceForAccounts?accountIds=1,2,3'
