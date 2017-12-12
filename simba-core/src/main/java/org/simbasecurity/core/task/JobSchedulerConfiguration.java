/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.simbasecurity.core.task;

import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private Trigger purgeExpiredTokensTrigger;
    @Autowired
    private Trigger markUsersForPasswordChangeTrigger;

    @Value("${quartz.jobstore.delegate:org.quartz.impl.jdbcjobstore.HSQLDBDelegate}")
    private String quartzJobstoreDelegate;

    @Bean(destroyMethod = "destroy")
    public SchedulerFactoryBean schedulerFactory() {
        Properties quartzProperties = new Properties();

        quartzProperties.put("org.quartz.threadPool.threadCount", "2");
        quartzProperties.put("org.quartz.jobStore.driverDelegateClass", quartzJobstoreDelegate);

        Trigger[] triggers = new Trigger[]{
                verifyAuditLogIntegrityTrigger,
                cleanUpAuditLogTrigger,
                purgeExpiredLoginMappingsTrigger,
                purgeExpiredSessionsTrigger,
                purgeExpiredTokensTrigger,
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
