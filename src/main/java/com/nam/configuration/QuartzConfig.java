package com.nam.configuration;

import com.nam.service.quartz.CarParkInfoSyncJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail carParkInfoJobDetail() {
        return JobBuilder.newJob(CarParkInfoSyncJob.class)
                .withIdentity("carParkInfoSyncJob")
                .storeDurably()
                .build();
    }

/*    @Bean
    public Trigger carParkInfoJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(10) // Adjust the interval as needed
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(carParkInfoJobDetail())
                .withIdentity("carParkInfoTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }*/
}
