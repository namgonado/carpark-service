package com.nam.configuration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class FlywayConfig {
    @Bean
    @ConditionalOnProperty(name = "fix.flyway.checksum", havingValue = "true", matchIfMissing = false)
    public FlywayMigrationStrategy repairFlyway() {
        return flyway -> {
            // repair each script's checksum
            flyway.repair();
            // before new migrations are executed
            flyway.migrate();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "fix.flyway.checksum", havingValue = "true", matchIfMissing = false)
    public FixChecksumCallback flywayCallback(@Lazy Flyway flyway) {
        return new FixChecksumCallback(flyway);
    }

    class FixChecksumCallback implements Callback {
        private final String removeMismatchQuery = "Delete from %s where version='%s'";
        private final Flyway flyway;

        @Autowired
        @Lazy
        private JdbcTemplate jdbcTemplate;

        public FixChecksumCallback(Flyway flyway) {
            this.flyway = flyway;
        }

        @Override
        public boolean supports(Event event, Context context) {
            return event == Event.BEFORE_REPAIR;
        }

        @Override
        public boolean canHandleInTransaction(Event event, Context context) {
            return true;
        }

        @Override
        public void handle(Event event, Context context) {
            try {
                flyway.validate();
            } catch (Exception exception) {
                String failedMessage = exception.getMessage();
                String database = flyway.getConfiguration().getTable();
                String version;
                if (failedMessage.contains("checksum mismatch")) {
                    Pattern pattern = Pattern.compile("([\\s\\S]*)version ([\\d]*)([\\s\\S]*)");
                    Matcher matcher = pattern.matcher(failedMessage);
                    matcher.matches();
                    version = matcher.group(2);
                    String query = String.format(removeMismatchQuery, database, version);

                    jdbcTemplate.execute(query);
                }
            }
        }

        @Override
        public String getCallbackName() {
            return "FixChecksumCallback";
        }
    }
}

