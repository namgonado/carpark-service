package com.nam.service.quartz;

import com.nam.provider.carpark.availability.CarParkAvailabilityProvider;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarParkInfoSyncJob implements Job {

    @Autowired
    private CarParkAvailabilityProvider carParkInfoProvider;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        carParkInfoProvider.poll();
    }
}