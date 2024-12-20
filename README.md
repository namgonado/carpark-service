# carpark-service
Service to locate the nearest car park with vacant parking slots

## Local installation
1. Need to be installed:
    - Java 17
    - Docker Engine 27.2+ or later (compose 2.29)
    - Maven 3.1+

## Build and Deploy Instructions

Follow these steps to build and deploy the Carpark Service project:

1. **Clone the Project**: Clone the repository to your local machine, checkout master branch:
   ```bash
   git clone git@github.com:namgonado/carpark-service.git
   git checkout master
   ```

2. **Compile the Spring Boot Application**: Navigate to the project's root folder and compile the project using one of the following options:
   - **Without tests**:
     ```bash
     mvn clean package -DskipTests
     ```
   - **With tests**:
     ```bash
     mvn clean package
     ```

3. **Deploy Docker Containers with Build**: In the same root folder, deploy the Docker containers and force the build of the Docker images using the following command:
   ```bash
   docker compose -f docker-compose.yml up --build --detach
   ```

4. **Verify Deployment**: Wait for the Docker deployment to complete successfully. Then, check the status and logs of the following containers:
   - carpark-service
   - redis-for-carpark
   - postgres-for-carpark

   Use the command below to view logs for a specific container (e.g., `carpark-service`):
   ```bash
   docker logs carpark-service
   ```
   **Note**: Most of the containers use default ports. There is a high probability that you may already have some Docker containers running locally with the same ports, which can cause the containers to fail to start up. If this happens, update the ports in the .env file to new, unused ports.

5. **Refresh the Deployment**: If you need to refresh the deployment, stop and remove all containers, networks, and volumes created by docker compose:
   ```bash
   docker compose -f docker-compose.yml down
   ```
   Then, redeploy the containers:
   ```bash
   docker compose -f docker-compose.yml up --build --detach
   ```
6. **Access the Server**: By default, the server is available at:
   [http://localhost:8088](http://localhost:8088)

## Configuration
### Application properties
The application is configured using properties in the `application.yaml` file. Refer to the file to update properties, then rebuild the application and redeploy the containers.

```yaml
carpark:
  provider:
    availability:
      external-source: 'https://api.data.gov.sg/v1/transport/carpark-availability'
      syncup:
        cron-job:
          enable: false  # Set to true to enable cron job polling
          interval: 60    # Interval in seconds
        cache: IN_MEMORY  # Possible values: IN_MEMORY, GLOBAL_REDIS
```
**Key Configuration Properties**

- `external-source`: The URL for the external car park availability data source.
- `syncup.cron-job.enable`: Set to `true` to enable periodic polling, otherwise `false`.
- `syncup.cron-job.interval`: Defines the polling interval in seconds.
- `cache`: Defines the cache mechanism to use. Options are:
    - `IN_MEMORY`: Cache data in memory.
    - `GLOBAL_REDIS`: Cache data in a global Redis instance.

### Deployment properties
The .env file contains Docker parameters for deployment. Update these parameters, especially the ports, as needed to avoid errors or conflicts with other running containers. Example:
```SERVER_PORT=8088
REDIS_URL=redis
REDIS_PORT=16379
POSTGRES_USER=root
POSTGRES_PASSWORD=Rootadmin1
POSTGRES_HOST=postgres
POSTGRES_PORT=15432
POSTGRES_SCHEMA=carpark
```
## APIs
1. Trigger Car Park Polling (Sync Up)
Manually trigger the car park polling sync up by sending a POST request to:

```http
POST http://localhost:8088/carparks/sync
```
2. Search for Nearest Car Park
   Search for the nearest car park location by providing latitude and longitude parameters in a GET request:

```http
GET http://localhost:8088/carparks/nearest?latitude=1.347643&longitude=103.957792&page=0&per_page=5
```
## Feature and Architect
### Modularization
- The application is modularized to separate functionality into distinct packages:
   - `CarParkGeoProvider`: Loads geographical information from a CSV file.
   - `CarParkAvailabilityProvider`: Polls availability data from a provided URL.
   - The car park provider package handles loading data from external sources and APIs.
- Each of these packages can be cut out and deployed as separate services if needed, making the architecture flexible and reusable.

### Extensible and Scalable
- **Car Park Polling Synchronization**:
   - Availability data is synchronized with an external data source using the `CarParkAvailabilityProvider`.
   - This provider uses two types of caching components to optimize data update inspections:
      - **In-Memory Cache**: Suitable for standalone services.
      - **Redis Global Cache**: Suitable for scaling across multiple services.
   - The caching mechanism is configurable via the `application.yml` file. Use the `carpark.provider.availability.syncup.cache` property to switch between caching options based on deployment needs.
- **Kafka Integration Possibility**:
  - Kafka is a superior alternative to the fixed-list polling approach for car park availability due to its numerous advantages:
     - It efficiently accumulates event messages over time, ensuring no data is lost.
     - Its asynchronous processing capabilities allow for better scalability and system resilience.
  - Despite these benefits, Kafka is not implemented in this version due to the lack of a defined message format in the requirements.
  - The existing classes and components are designed for extensibility, allowing Kafka integration in the future without significant architectural changes.

## Contacts
- Solution Architect [nam le-hong](namgonado@gmail.com)
