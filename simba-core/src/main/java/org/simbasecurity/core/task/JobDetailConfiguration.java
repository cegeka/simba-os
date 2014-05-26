package org.simbasecurity.core.task;

import org.quartz.JobDetail;
import org.simbasecurity.core.spring.quartz.BeanInvokingJobDetailFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobDetailConfiguration {

    private static final String SIMBA_QUARTZ_GROUP = "Simba";

    @Bean
    public JobDetail verifyAuditLogIntegrityDetail() throws Exception {
        BeanInvokingJobDetailFactoryBean bean = new BeanInvokingJobDetailFactoryBean();
        bean.setTargetBeanName("verifyAuditLogIntegrityTask");
        bean.setExecutionMethod("execute");
        bean.setName("Verify if the Archive audit log wasnt tampered with");
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    @Bean
    public JobDetail cleanUpAuditLogJobDetail() throws Exception {
        BeanInvokingJobDetailFactoryBean bean = new BeanInvokingJobDetailFactoryBean();
        bean.setTargetBeanName("cleanUpAuditLogTask");
        bean.setExecutionMethod("execute");
        bean.setName("Clean up audit log");
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    @Bean
    public JobDetail purgeExpiredSessionsJobDetail() throws Exception {
        BeanInvokingJobDetailFactoryBean bean = new BeanInvokingJobDetailFactoryBean();
        bean.setTargetBeanName("purgeExpiredSessionsTask");
        bean.setExecutionMethod("execute");
        bean.setName("Purge Expired Sessions");
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    @Bean
    public JobDetail markUsersForPasswordChangeDetail() throws Exception {
        BeanInvokingJobDetailFactoryBean bean = new BeanInvokingJobDetailFactoryBean();
        bean.setTargetBeanName("markUsersForPasswordChangeTask");
        bean.setExecutionMethod("execute");
        bean.setName("Mark Users For Password Change");
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    @Bean
    public JobDetail purgeExpiredLoginMappingsJobDetail() throws Exception {
        BeanInvokingJobDetailFactoryBean bean = new BeanInvokingJobDetailFactoryBean();
        bean.setTargetBeanName("purgeExpiredMappingsTask");
        bean.setExecutionMethod("execute");
        bean.setName("Purge Expired Login Mappings");
        bean.setGroup(SIMBA_QUARTZ_GROUP);
        bean.afterPropertiesSet();
        return bean.getObject();
    }

}
