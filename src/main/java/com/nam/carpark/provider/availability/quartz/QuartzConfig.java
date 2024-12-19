package com.nam.carpark.provider.availability.quartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Value("${carpark.provider.availability.syncup.cron-job.interval:20}")
    private int intervalSecond;
    @Bean
    public JobDetail carParkInfoJobDetail() {
        return JobBuilder.newJob(CarParkInfoSyncJob.class)
                .withIdentity("carParkInfoSyncJob")
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "carpark.provider.availability.syncup.cron-job.enable", havingValue = "true")
    public Trigger carParkInfoJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(intervalSecond) // Adjust the interval as needed
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(carParkInfoJobDetail())
                .withIdentity("carParkSyncupTrigger")
                .startNow()
                .withSchedule(scheduleBuilder)
                .build();
    }
}
