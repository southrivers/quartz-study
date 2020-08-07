package com.hw;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * 基于jobDataMap 实现作业之间有依赖关系的调度
 */
public class JobDataMapDAG {



    public static void main(String[] args) throws Exception {

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        /**
         * 这一步是模拟一个作业转换图，当前是job之间的关系的绑定，还可以使用触发trigger的方式，使用trigger来完成多个作业的触发
         */
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(StartJob.class.getName(), TransformJob.class.getName());
        jobDataMap.put(TransformJob.class.getName(), EndJob.class.getName());

        JobDetail startJob = JobBuilder.newJob(StartJob.class)
                .withDescription("this is description")
                .withIdentity("hello", "wes")
                .usingJobData(jobDataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withDescription("this is trigger")
                .withIdentity("trigger", "wes")
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(20))
                .startNow()
                .build();

        scheduler.scheduleJob(startJob, trigger);
        scheduler.start();

        Thread.currentThread().join();
    }

    public static class StartJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

            System.out.print("start ---------------> ");

            // 完成start job之后调度到下面一个作业中
            String nextJob = jobExecutionContext.getJobDetail().getJobDataMap().getString(this.getClass().getName());
            try {
                ((Job)(Class.forName(nextJob).newInstance())).execute(jobExecutionContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class TransformJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.print("transform ---------------> ");

            // 完成transform job之后调度到下面一个作业中
            String nextJob = jobExecutionContext.getJobDetail().getJobDataMap().getString(this.getClass().getName());
            try {
                ((Job)(Class.forName(nextJob).newInstance())).execute(jobExecutionContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class EndJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

            System.out.println("end");
        }
    }
}
