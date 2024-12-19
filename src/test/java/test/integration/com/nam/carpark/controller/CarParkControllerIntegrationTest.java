package test.integration.com.nam.carpark.controller;

import com.nam.carpark.ServiceApplication;
import com.nam.carpark.model.dto.SearchCarParkResponse;
import com.nam.carpark.utils.CoordinateConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.environment.TestServerConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestServerConfig.EnvInitializer.class, classes = {ServiceApplication.class})
public class CarParkControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    public void testSyncAndFindNearestCarParks() {
        // Send POST request to sync car park data
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> syncResponse = restTemplate.exchange(baseUrl + "/carparks/sync", HttpMethod.POST, entity, String.class);
        assertEquals(200, syncResponse.getStatusCodeValue());

        // Send GET request to find nearest car parks
        String url = baseUrl + "/carparks/nearest?latitude=1.347643&longitude=103.957792&page=0&per_page=5";
        ResponseEntity<SearchCarParkResponse[]> response = restTemplate.getForEntity(url, SearchCarParkResponse[].class);
        assertEquals(200, response.getStatusCodeValue());

        SearchCarParkResponse[] carParks = response.getBody();
        assertNotNull(carParks);
        assertEquals(5, carParks.length);

        // Verify the order by distance
        for (int i = 0; i < carParks.length - 1; i++) {
            double distance1 = CoordinateConverter.distance(1.347643, 103.957792, carParks[i].getLatitude(), carParks[i].getLongitude());
            double distance2 = CoordinateConverter.distance(1.347643, 103.957792, carParks[i + 1].getLatitude(), carParks[i + 1].getLongitude());
            assertEquals(true, distance1 <= distance2);
        }
    }
}
