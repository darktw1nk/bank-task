# Revolut backend-task project

### Information

#### Services

Accounts

| HTTP METHOD | PATH           |
|-------------|----------------|
|   GET       | /accounts      |
| GET         | /accounts/{id} |
| POST        | /accounts      |
| PUT         | /accounts/{id} |
| DELETE      | /accounts/{id} |

Transactions

| HTTP METHOD | PATH               |
|-------------|--------------------|
| GET         | /transactions      |
| GET         | /transactions/{id} |
| POST        | /transactions      |

* OpenAPI yaml is also available in META-INF folder 


#### Technology stack

##### Web

* Quarkus
* RESTEasy 

##### Database

* H2
* Hibernate ORM
* Narayana JTA

##### Tests

* REST Assured
* Mockito
* JUnit 5

##### JSON bindings

*  Jackson


#### Tests instructions

Please run `./mvn test` for unit tests and `./mvn verify` for integtration tests


#### Packaging and running the application

The application is packageable using `./mvn package`.
It produces the executable `backend-task-1.0-SNAPSHOT-runner.jar` file in `/target` directory

The application is now runnable using `java -jar backend-task-1.0-SNAPSHOT-runner.jar`.
