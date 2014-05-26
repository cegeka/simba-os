package org.simbasecurity.core.task;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

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
    public SimpleTriggerBean verifyAuditLogIntegrityTrigger() throws ParseException {
        SimpleTriggerBean bean = new SimpleTriggerBean();
        bean.setJobDetail(verifyAuditLogIntegrityDetail);
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.setRepeatCount(REPEAT_FOREVER);
        bean.setRepeatInterval(TimeUnit.MINUTES.toMillis(5));
        return bean;
    }

    @Bean(initMethod = "afterPropertiesSet")
    public SimpleTriggerBean cleanUpAuditLogTrigger() throws ParseException {
        SimpleTriggerBean bean = new SimpleTriggerBean();
        bean.setJobDetail(cleanUpAuditLogJobDetail);
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.setRepeatCount(REPEAT_FOREVER);
        bean.setRepeatInterval(TimeUnit.MINUTES.toMillis(5));
        return bean;
    }

    @Bean(initMethod = "afterPropertiesSet")
    public SimpleTriggerBean purgeExpiredLoginMappingsTrigger() throws ParseException {
        SimpleTriggerBean bean = new SimpleTriggerBean();
        bean.setJobDetail(purgeExpiredLoginMappingsJobDetail);
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.setRepeatCount(REPEAT_FOREVER);
        bean.setRepeatInterval(TimeUnit.MINUTES.toMillis(1));
        return bean;
    }

    @Bean(initMethod = "afterPropertiesSet")
    public SimpleTriggerBean purgeExpiredSessionsTrigger() throws ParseException {
        SimpleTriggerBean bean = new SimpleTriggerBean();
        bean.setJobDetail(purgeExpiredSessionsJobDetail);
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.setRepeatCount(REPEAT_FOREVER);
        bean.setRepeatInterval(TimeUnit.MINUTES.toMillis(1));
        return bean;
    }

    @Bean(initMethod = "afterPropertiesSet")
    public CronTriggerBean markUsersForPasswordChangeTrigger() throws Exception {
        CronTriggerBean bean = new CronTriggerBean();
        bean.setJobDetail(markUsersForPasswordChangeDetail);
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.setCronExpression(new CronExpression("0 0 5 * * ?"));
        return bean;
    }
}
