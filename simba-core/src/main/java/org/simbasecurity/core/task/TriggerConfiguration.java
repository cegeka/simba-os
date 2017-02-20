package org.simbasecurity.core.task;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class TriggerConfiguration {

    private static final String SIMBA_QUARTZ_GROUP = "Simba";
    private static final int REPEAT_FOREVER = -1;

    @Autowired
    private JobDetail verifyAuditLogIntegrityDetail;

    @Autowired
    private JobDetail cleanUpAuditLogJobDetail;

    @Autowired
    private JobDetail purgeExpiredLoginMappingsJobDetail;

    @Autowired
    private JobDetail purgeExpiredSessionsJobDetail;

    @Autowired
    private JobDetail markUsersForPasswordChangeDetail;

    @Bean(initMethod = "afterPropertiesSet")
    public SimpleTriggerFactoryBean verifyAuditLogIntegrityTrigger() throws ParseException {
        return createSimpleTrigger(verifyAuditLogIntegrityDetail, TimeUnit.MINUTES.toMillis(5));
    }

    @Bean(initMethod = "afterPropertiesSet")
    public SimpleTriggerFactoryBean cleanUpAuditLogTrigger() throws ParseException {
        return createSimpleTrigger(cleanUpAuditLogJobDetail, TimeUnit.MINUTES.toMillis(5));
    }

    @Bean(initMethod = "afterPropertiesSet")
    public SimpleTriggerFactoryBean purgeExpiredLoginMappingsTrigger() throws ParseException {
        return createSimpleTrigger(purgeExpiredLoginMappingsJobDetail, TimeUnit.MINUTES.toMillis(1));
    }

    @Bean(initMethod = "afterPropertiesSet")
    public SimpleTriggerFactoryBean purgeExpiredSessionsTrigger() throws ParseException {
        return createSimpleTrigger(purgeExpiredSessionsJobDetail, TimeUnit.MINUTES.toMillis(1));
    }

    @Bean(initMethod = "afterPropertiesSet")
    public CronTriggerFactoryBean markUsersForPasswordChangeTrigger() throws Exception {
        return createCronTrigger(markUsersForPasswordChangeDetail, "0 0 5 * * ?");
    }

    private SimpleTriggerFactoryBean createSimpleTrigger(JobDetail jobDetail, long repeatIntervalInMillis) {
        SimpleTriggerFactoryBean bean = new SimpleTriggerFactoryBean();
        bean.setJobDetail(jobDetail);
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.setRepeatCount(REPEAT_FOREVER);
        bean.setRepeatInterval(repeatIntervalInMillis);
        return bean;
    }

    private CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setJobDetail(jobDetail);
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.setCronExpression(cronExpression);
        return bean;
    }
}
