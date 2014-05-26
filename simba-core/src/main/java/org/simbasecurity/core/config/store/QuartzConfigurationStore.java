/*
 * Copyright 2011 Simba Open Source
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
 */
package org.simbasecurity.core.config.store;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class QuartzConfigurationStore implements ConfigurationStore {

    private static final String SIMBA_JOB_GROUP = "Simba";

    @Autowired
    @Qualifier("configurableJobNames")
    private EnumMap<ConfigurationParameter, String> configurableJobNames;

    @Autowired private Scheduler scheduler;

    public String getValue(ConfigurationParameter parameter) {
        try {
            Trigger trigger = findTrigger(parameter);

            if (trigger instanceof SimpleTrigger) {
                long repeatInterval = ((SimpleTrigger) trigger).getRepeatInterval();
                return String.valueOf(parameter.getTimeUnit().convert(repeatInterval, TimeUnit.MILLISECONDS));
            } else if (trigger instanceof CronTrigger) {
                return ((CronTrigger) trigger).getCronExpression();
            } else {
                throw new IllegalStateException("Type " + trigger.getClass().getName() + " not handled");
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getValueList(ConfigurationParameter parameter) {
        throw new UnsupportedOperationException("Quartz configuration store does not support storing a list of values.");
    }

    public String setValue(ConfigurationParameter parameter, String value) {
        try {
            JobDetail jobDetail = findJob(parameter);
            Trigger trigger = findTrigger(parameter);
            Trigger newTrigger;

            String oldValue;

            if (trigger instanceof SimpleTrigger) {
                newTrigger = new SimpleTrigger(trigger.getName(), SIMBA_JOB_GROUP, SimpleTrigger.REPEAT_INDEFINITELY, TimeUnit.MILLISECONDS.convert(Long.parseLong(value), parameter.getTimeUnit()));
                newTrigger.setStartTime(trigger.getNextFireTime());
                oldValue = String.valueOf(((SimpleTrigger) trigger).getRepeatInterval());
            } else if (trigger instanceof CronTrigger) {
                newTrigger = new CronTrigger(trigger.getName(), SIMBA_JOB_GROUP, value);
                oldValue = ((CronTrigger) trigger).getCronExpression();
            } else {
                throw new IllegalStateException("Type " + trigger.getClass().getName() + " not handled");
            }
            newTrigger.setJobName(jobDetail.getName());
            newTrigger.setJobGroup(SIMBA_JOB_GROUP);

            scheduler.rescheduleJob(trigger.getName(), SIMBA_JOB_GROUP, newTrigger);
            return oldValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> setValueList(ConfigurationParameter parameter, List<String> valueList) {
        throw new UnsupportedOperationException("Quartz configuration store does not support storing a list of values.");
    }

    private Trigger findTrigger(ConfigurationParameter parameter) throws SchedulerException {
        if (!configurableJobNames.containsKey(parameter)) {
            throw new IllegalArgumentException("No job detail bound to parameter '" + parameter + "'");
        }
        String jobName = configurableJobNames.get(parameter);
        return scheduler.getTriggersOfJob(jobName, SIMBA_JOB_GROUP)[0];
    }

    private JobDetail findJob(ConfigurationParameter parameter) throws SchedulerException {
        if (!configurableJobNames.containsKey(parameter)) {
            throw new IllegalArgumentException("No job detail bound to parameter '" + parameter + "'");
        }

        String jobName = configurableJobNames.get(parameter);
        return scheduler.getJobDetail(jobName, SIMBA_JOB_GROUP);
    }
}
