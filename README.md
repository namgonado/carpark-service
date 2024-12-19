# carpark-service
Service to locate the nearest car park with vacant parking slots

## Local installation
1. Need to be installed:
    - Java 17
    - Docker Engine 27.2+ or later (compose 2.29)
    - Maven 3.1+

# Carpark Service

## Build and Deploy Instructions

Follow these steps to build and deploy the Carpark Service project:

1. **Clone the Project**: Clone the repository to your local machine:
   ```bash
   git clone git@github.com:namgonado/carpark-service.git
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
   - `carpark-service`
   - `redis-for-carpark`
   - `postgres-for-carpark`

   Use the command below to view logs for a specific container (e.g., `carpark-service`):
   ```bash
   docker logs carpark-service
   ```

5. **Refresh the Deployment**: If you need to refresh the deployment, stop and remove all containers, networks, and volumes created by `docker compose`:
   ```bash
   docker compose -f docker-compose.yml down
   ```
   Then, redeploy the containers:
   ```bash
   docker compose -f docker-compose.yml up --build --detach
   ```

6. **Access the Server**: By default, the server is available at:
   [http://localhost:8088](http://localhost:8088)
## Feature and Configuration
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
