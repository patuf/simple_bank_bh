# Simple Bank
For assignment details please see [the assignment](./assignment.md) 

# Intro
The solution consists of two modules, each of which has its own maven infrastructure and a Spring Boot application:
* ./accounts - encompassing the accounts CR(UD were not part of the assignment) operations and the facade of the reporting functionality.
* ./transactions - dealing with the transactions' CR operations and reporting. For avoiding ambiguity with database transactions,
this module will henceforth be referred to as "Bank Transactions"

N.B: The assignment stated that accounts and transactions should be different services, and I wasn't sure if the
author meant "separate microservices" or just "separate @Service classes". So I went for what I perceived as the
more complex alternative, just in case.

The asynchronous part of the communication between the two microservices can be done either through Kafka messaging
or with asynchronous REST calls. The next section explains how to choose the scenario you want to use. 

# Running the solution
## Requirements
### Software
* Java 11 
* Maven 3
* If you want to run the solution in Kafka messaging mode, you also need the latest version of Docker

### Networking
* The web server of Accounts runs on port 8080
* The web server of Bank Transactions runs on port 8081
* Kafka and Zookeeper (if used) require their default ports 2181,2888,3888 for Zookeeper and 9092 for Kafka 

The user needs only to interact with the Accounts service, as it also acts as the API Gateway and composition facade.

The communication between Accounts and Bank Transactions is mostly unidirectional:
* Synchronous HTTP calls for the reporting functionality
* Asynchronous HTTP or Kafka calls for the Account + Bank Transaction creation functionality.


If inconvenient, all ports can be changed, but the respective settings in both the application.properties files 
need to be updated accordingly.

## Starting
You'll need to clone the repository and pull the latest commit from its main branch.

### Start in "HTTP messaging" mode
This is the simpler way to start, as it doesn't require starting Zookeeper and Kafka

#### 1) Start the Accounts service

Navigate to the ./accounts subdirectory and then:
    
    mvn spring-boot:run -Dspring.profiles.active=dev,messages-http

#### 2) Start the Bank Transactions service

Navigate to the ./transactions subdirectory and then:

    mvn spring-boot:run -Dspring.profiles.active=dev,messages-http

### Start in "Kafka messaging" mode
#### 1) Start a Zookeeper instance:

    docker run -it --rm --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 quay.io/debezium/zookeeper:1.9

#### 2) Start a Kafka instance:

    docker run -it --rm --name kafka -p 9092:9092 --link zookeeper:zookeeper quay.io/debezium/kafka:1.9

#### 3) Start the Accounts service
Navigate to the ./accounts subdirectory and then:

    mvn spring-boot:run -Dspring.profiles.active=dev,messages-kafka

#### 4) Start the Bank Transactions service

Navigate to the ./transactions subdirectory and then:

    mvn spring-boot:run -Dspring.profiles.active=dev,messages-kafka

# Usage
The solution is built as a HATEOAS-compliant REST service. You have two usage options:
* Through its Swagger UI interface in your browser, on http://localhost:8080/swagger-ui/index.html
* Through an HTTP client like curl or Postman. The OpenApi documentation is available at http://localhost:8080/v3/api-docs

Obviously you need the Accounts service running, for there two URLs to work.

## Pre-populated data
Account's database is initiated with the following:
* CUSTOMER table contains 5 customers, with IDs 1 to 5
* ACCOUNT table contains 25 accounts for customer with ID 1
* CREATE_TRANSACTION_COMMAND is empty

Bank Transactions' database is initiated with the following:
* BANK_TRANSACTION contains 25 transactions for account 1 with total balance -440, and one transaction for 
  account 2 amounting to 1000.
  
N.B. Implementing currencies was not the focus of my solution, as it would have brought additional complexity in 
converting between them when calculating balances.

## Description of the endpoints
There are 5 endpoints available. These will be briefly described here in terms of what they do. In case you need to
further examine their parameters or test their execution, you are encouraged to visit the [Accounts' Swagger UI](http://localhost:8080/swagger-ui/index.html)

* POST: **/rest/v1.0/account**: Creates a new account for an existing customer. If the customer doesn't exist,
returns a 404 error, otherwise, returns the created Account and HATEOAS links.
  
* GET: **/rest/v1.0/account/{accountId}**: Retrieves the account with ID *accountId*. If the account doesn't exist,
returns a 404 error, otherwise, returns the created Account and HATEOAS links.
  
* GET **/rest/v1.0/report/customers**: Retrieves a paged list of all customers with their total balance 
  (the sum of all the transactions on all of their accounts), with HATEOAS links. Page number and size can be
  specified with the standard request parameters *?size=N&page=M*. Default page size is 20 items. Keep in mind that 
  the Swagger-ui sets the page size to 1 by default. Sorting is not supported and sorting parameters will be ignored.
  Results are shown in their natural order.

* GET **/rest/v1.0/report/customerAccounts/{customerId}**: Retrieves a paged list of all accounts for customerId, 
  with the balance for each account (The sum of all transactions for each account). If the customerId doesn't 
  exist in the database, returns an empty list.
  
* GET **/rest/v1.0/report/accountTransactions/{accountId}**: Retrieves a pages list of all the transactions for
  accountId. If accountId doesn't exist, returns an empty list.

## Running the tests
The commands are the same in both Accounts and Bank Transaction modules:

### Running the unit tests
Navigate to the chosen subdirectory (./accounts or ./transactions) and type:

    mvn test -Punit-test

### Running the integration tests
Navigate to the chosen subdirectory and type:

    mvn test -Pintegration-test

## Accessing H2's console 
Both services have their H2 console exposed

Accounts' H2 console: 

    http://localhost:8080/h2-console/login.jsp

Banks Transactions' H2 console:

    http://localhost:8081/h2-console/login.jsp

Connection details are the same for both databases:

| Property      | Value
|---------------|-------------------
| Driver class  | org.h2.Driver
| JDBC URL      | jdbc:h2:mem:testdb
| User Name     | sa
| Password      | password

# Short notes on architecture
* In-memory database is used (as required) for both normal operation and tests. Running tests with the services started will result
  in the data being wiped, and the services will need to be restarted to allow reinitialization with the pre-populated
  data from data.sql
* A naive attempt at home-made Transaction Outbox is implemented for the asynchronous command message being sent during
  transaction creation. It needs a lot of improvements, and I'm guessing this may be one of the topics for discussion
  during the technical interview.
* The reporting functionality is somewhat detached with the idea that it would ease forking out a microservice
  dedicated to reporting service. In a reporting microservice, it is acceptable for tables to be denormalized. Therefore,
  one of the tables was left intentionally denormalized, for making reporting easier (It is left as a small
  exercise to the evaluator to find out which table and how it is denormalized)
* The logical division of the microservices in this solution is not ideal and was purely dictated by
  what is probably my over-complication of a misunderstood requirement.

# A concise list of potential improvements
* Transaction outbox implementation - should have retries, ability to scale horizontally, Dead Letter Queue, acknowledgement of
  successful execution and a rollback transaction.
* The microservice split - transactions don't really need to be separate microservice
* Implement CRQS pattern
* Validations - use min max allowed values 
* Testing - more negative scenarios
* Use currencies. The balance report would then include a join to a currency conversion rates table
* Use ZonedDateTime or store everything converted to UTC.
* Swagger/OpenApi can be better configured - links section is empty, page size shouldn't be 1 by default, some response types are undocumented.
