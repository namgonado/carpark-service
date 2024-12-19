# carpark-service
Service to locate the nearest car park with vacant parking slots

## Local installation
1. Need to be installed:
    - Java 17
    - Docker Engine 27.2+ or later (compose 2.29)
    - Maven 3.1+

## Build
To build the project, execute the following maven goal:
1. Clone project 
   ``` 
   git clone git@github.com:namgonado/carpark-service.git
   ```
2. Open a terminal, navigate to the project's root folder, and execute the following command to compile the Spring Boot application.
   ```$xslt
   mvn clean package -DskipTests
   ```
3.  In the project root folder, execute the following command to deploy the Docker containers.
   ```$xslt
   docker compose -f docker-compose.yml up --detach
   ```
4. Wait for the Docker deployment to complete successfully. Check the status and logs of the following containers:
   * carpark-service
   * redis-for-carpark
   * postgres-for-carpark
5. By default, the server can be accessed at [localhost:8088](http://localhost:8088)

## Feature and Configuration
Car park availability information is synchronized with an external data source using the CarParkAvailabilityProvider. This provider utilizes two caching components to enhance the inspection process for data update status:

In-memory cache: Used for a standalone service.
Redis global cache: Used when scaling up across multiple services.
To switch between these caching components, set the appropriate value for provider.syncup.cache in the application.yml configuration file.

## Contacts
- Solution Architect [nam le-hong](namgonado@gmail.com)
