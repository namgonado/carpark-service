package testEnvironment;

import com.redis.testcontainers.RedisContainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestConfiguration
@Testcontainers
public class TestServerConfig {
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("carpark")
            .withUsername("root")
            .withPassword("Rootadmin1");

    @Container
    private static RedisContainer redisContainer = new RedisContainer(
            RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

    static {
        postgresContainer.start();
        redisContainer.start();
    }

    public static class EnvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=jdbc:postgresql://%s:%s/%s".formatted(
                            postgresContainer.getHost(),
                            postgresContainer.getMappedPort(5432),
                            postgresContainer.getDatabaseName()
                    ),
                    "spring.datasource.username=%s".formatted(postgresContainer.getUsername()),
                    "spring.datasource.password=%s".formatted(postgresContainer.getPassword()),
                    "spring.datasource.driver-class-name=org.postgresql.Driver",
                    "spring.data.redis.host=%s".formatted(redisContainer.getHost()),
                    "spring.data.redis.port=%s".formatted(redisContainer.getMappedPort(6379))
            ).applyTo(applicationContext);
        }
    }
}