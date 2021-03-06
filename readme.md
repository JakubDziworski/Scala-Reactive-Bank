
[![Build Status](https://travis-ci.org/JakubDziworski/Scala-Reactive-Bank.svg?branch=master)](https://travis-ci.org/JakubDziworski/Scala-Reactive-Bank)

## Technologies used
* Play
* Specs2 (each service has tests in ```test``` subdirectories)
* Slick with PostgreSQL
* MongoDB with ReactiveMongo driver
* Akka actors (see ```transactions-service/app/actors```)
* Swagger (all controllers are documented and available via /v1/swagger-ui)
* Docker (Docker Compose)

## Running with docker

Make sure ports 5432 (postgres),27016(mongo),9000,9001,9002 are available.


Run
```bash
docker-compose up
```
Once the containers are up and running you can access services with swagger via:

* http://localhost:9000/v1/swagger-ui <-- transactions service
* http://localhost:9001/v1/swagger-ui <-- settings service 
* http://localhost:9002/v1/swagger-ui <-- accounts service

## Running manually

* Setup postgres and provide configuration credentials in `account-settings-service/conf/application.conf`.
No schemas or tables are required - just user and database.
* Setup mongo and provide url in `transactions-settings-service/conf/application.conf`.

* Cd into each service and run:
  ```bash
  sbt -Dhttp.port=<port> run
  ```
  where recommended ports are: 9000 for transaction service, 9001 for settings and 9002 for accounts.
