package com.hw;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class QuickStart {

    public static void main(String[] args) throws Exception {

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail jobDetail = JobBuilder.newJob(StartJob.class).withIdentity("quick start", "wes").build();
        Trigger trigger = TriggerBuilder.newTrigger().withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(10)).startNow().build();
        scheduler.start();
        scheduler.scheduleJob(jobDetail, trigger);
        Thread.currentThread().join();
    }

    public static class StartJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("quick start!");
        }
    }
}
