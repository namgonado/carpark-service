package com.nam.carpark.provider.availability.configuration;

import com.nam.carpark.provider.availability.quartz.QuartzConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(name = "carpark.provider.availability.type", havingValue = "POLLING")
@Import(QuartzConfig.class)
public class CarParkPollingConfig {
    // To enable the polling car park provider
}