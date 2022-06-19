### I18N
I18N is omitted, as it is not among the goals of the assignment

### Frontend
Rest service will be built with HATEOAS. It is not a requirement but could, to some extent, mitigate the lack of UI, if lack of UI happens.

### Transaction Outbox
A Spring Integration polling adapter is used for the transaction-outbox
Scalability is currently not solved, but also out of scope for the assignment

Alternatives: https://debezium.io/documentation/ <br/>
Debezium is more mature and Spring-prepared, but doesn't support in-memory databases<br/>
Example using Debezium: https://debezium.io/documentation/ 

### Security
No secutiry will be implemented, nor is needed by the requirements.

### Serving results
* endpoind: /customers + number of accounts & total balance, pageable
* endpoint: /all accounts for customer, with date of creation and balance, pageable
* endpoint: /all transactions for an account, pageable

### Aggregate Roots
Both Account and Customer are considered aggregate roots. Trying to keep the implementation closer to the real world , each of these entities seems to have its own complexity.
Keeping them as separate aggregates would also help forking out a separate Users microservice.