package com.autowire;



import java.io.IOException;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import com.SchedulerConfig.AutowiringSpringBeanJobFactory;
import com.job.SimpleJob;

@Configuration
public class SchedulerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerConfig.class);

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
    	
    	System.out.println("::::::::::::::::::::::::::::::::::::::JobFactory");
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, Trigger simpleJobTrigger)
            throws IOException {
    	System.out.println("::::::::::::::::::::::::::::::::::::::SchedulerFactoryBean");
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());
        factory.setTriggers(simpleJobTrigger);
       // LOG.info("starting jobs....");
        return factory;
    }

    /*@Bean
    public SimpleTriggerFactoryBean simpleJobTrigger(@Qualifier("simpleJobDetail") JobDetail jobDetail,
            @Value("${simplejob.frequency}") long frequency) {
      //  LOG.info("simpleJobTrigger");

        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(0L);
        factoryBean.setRepeatInterval(frequency);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factoryBean;
    }*/
    // Use this method for creating cron triggers instead of simple triggers:
    @Bean
	private static CronTriggerFactoryBean createCronTrigger(@Qualifier("simpleJobDetail") JobDetail jobDetail, @Value("${batchjob.cronExpression}")String cronExpression) {
    	System.out.println("::::::::::::::::::::::::::::::::::::::CronTriggerFactoryBean");
    	CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        return factoryBean;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
    	System.out.println("::::::::::::::::::::::::::::::::::::::quartzProperties");
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    public JobDetailFactoryBean simpleJobDetail() {
    	System.out.println("::::::::::::::::::::::::::::::::::::::JobDetailFactoryBean");
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(SimpleJob.class);
        factoryBean.setDurability(true);
        return factoryBean;
    }
}