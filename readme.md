## Technologies used
* Play
* Specs2 (each service has tests in ```test``` subdirectories)
* Slick (see ```SettingsDao.scala```)
* Akka actors (see ```transactions-service/app/actors```)
* Swagger (all controllers are documented and available for testing via /v1/swagger-ui)
* Docker (Docker Compose)

## Running with docker

Make sure ports 5432,9000,9001,9002 are available.

Run
```bash
docker-compose up
```
Once the containers are up and running you can access services with swagger via:

* http://localhost:9000/v1/swagger-ui <-- transactions service
* http://localhost:9001/v1/swagger-ui <-- settings service 
* http://localhost:9002/v1/swagger-ui <-- accounts service

## Running manually

Setup postgres database with credentials specified in `account-settings-service/conf/application.conf`.
No schemas or tables are required - just user and database.

Cd into each service and run:

```bash
sbt -Dhttp.port=<port> run
```

where recommended port are: 9000 for transaction service, 9001 for settings and 9002 for accounts.
