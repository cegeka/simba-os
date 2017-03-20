package org.simbasecurity.core.task;

import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class JobSchedulerConfiguration {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Trigger verifyAuditLogIntegrityTrigger;
    @Autowired
    private Trigger cleanUpAuditLogTrigger;
    @Autowired
    private Trigger purgeExpiredLoginMappingsTrigger;
    @Autowired
    private Trigger purgeExpiredSessionsTrigger;
    @Autowired
    private Trigger markUsersForPasswordChangeTrigger;

    @Bean(destroyMethod = "destroy")
    public SchedulerFactoryBean schedulerFactory() {
        Properties quartzProperties = new Properties();

        quartzProperties.put("org.quartz.threadPool.threadCount", "2");
        quartzProperties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.HSQLDBDelegate");

        Trigger[] triggers = new Trigger[] {
           verifyAuditLogIntegrityTrigger,
           cleanUpAuditLogTrigger,
           purgeExpiredLoginMappingsTrigger,
           purgeExpiredSessionsTrigger,
           markUsersForPasswordChangeTrigger,
        };

        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setWaitForJobsToCompleteOnShutdown(false);
        bean.setApplicationContextSchedulerContextKey("applicationContext");
        bean.setQuartzProperties(quartzProperties);
        bean.setTriggers(triggers);

        return bean;
    }
}
