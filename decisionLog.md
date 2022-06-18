### I18N
I18N is omitted, as it is not among the goals of the assignment

### Frontend
Rest service will be built with HATEOAS. It is not a requirement but could, to some extent, mitigate the lack of UI, if lack of UI happens.

### Transaction Outbox
The library used is: github.com/gruelbox/transaction-outbox
Pros: It supports H2
Cons: It is a bit bare and requires some boilerplate, but will suffice for our example.

Alternatives: https://debezium.io/documentation/ <br/>
Debezium is more mature and Spring-prepared, but doesn't support in-memory databases<br/>
Example using Debezium: https://debezium.io/documentation/ 
