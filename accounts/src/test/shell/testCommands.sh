docker run -it --rm --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 quay.io/debezium/zookeeper:1.9
docker run -it --rm --name kafka -p 9092:9092 --link zookeeper:zookeeper quay.io/debezium/kafka:1.9

curl -X POST -H 'Content-Type: application/json' http://localhost:8080/rest/v1.0/account -d '{"customerId": 1, "initialCredit": 1}'
curl -X GET -H 'Content-Type: application/json' 'http://localhost:8080/rest/v1.0/account/1' | jq .

curl -X GET -H 'Content-Type: application/json' 'http://localhost:8080/rest/v1.0/report/customers?page=1&size=2' | jq .
curl -X GET -H 'Content-Type: application/json' 'http://localhost:8080/rest/v1.0/report/customerAccounts/1?page=1&size=2' | jq .
curl -X GET -H 'Content-Type: application/json' 'http://localhost:8080/rest/v1.0/report/accountTransactions/1'