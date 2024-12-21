package com.nam.carpark.provider.availability.configuration;

import com.nam.carpark.provider.availability.CarParkAvailabilityProvider;
import com.nam.carpark.provider.availability.CarParkAvailabilityProviderImpl;
import com.nam.carpark.provider.availability.quartz.CarParkInfoSyncJob;
import com.nam.carpark.provider.availability.quartz.QuartzConfig;
import org.quartz.JobDetail;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(name = "carpark.provider.availability.type", havingValue = "POLLING")
@Import(QuartzConfig.class)
public class CarParkPollingConfig {
    // To enable the polling car park provider
    @Bean
    public CarParkAvailabilityProvider carParkAvailabilityProvider() {
        return new CarParkAvailabilityProviderImpl();
    }

    @Bean
    public CarParkInfoSyncJob carParkInfoSyncJob() {
        return new CarParkInfoSyncJob();
    }
}