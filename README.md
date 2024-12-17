# cms-media-service
Service for news and podcast management.

## Local installation
1. Need to be installed:
    - Java 17
    - Docker Engine 19.03.0+ (Or latest, because we are using [Docker compose 3.8](https://docs.docker.com/compose/compose-file/compose-versioning/))
    - Maven 3.1+
    - Redis 7
    - Postgres 15
2. To run project you have to set up the following environment variables:
    - Postgres database connection - refer to application-dev.yml for more information
    - Redis connection - refer to application-dev.yml for more information
    - AWS S3 connection - refer to application-dev.yml for more information
3. By default, server is running on [localhost:8087](http://localhost:8087)
4. Swagger is available by [/swagger-ui.html](http://localhost:8087/swagger-ui.html) path

## Test
To be implemented...

## Build
To build the project, execute the following maven goal:
1. Clone project https://github.com/Robustrade/kulu-parent
2. Install to you local parent bom
   ```$xslt
   mvn clean install
   ```
3. Via terminal, execute the following scripts:
   <br/>**Development environment without tests**:
   ```$xslt
   mvn clean install -Dmaven.test.skip=true
   ```
     
## Info
1. Please read application-dev.yml for possible values.
2. Find the environment variable values from [Contacts](#contacts).

## Contacts
- Solution Architect [nam le-hong](nam.lehong@morsoftware.com)
