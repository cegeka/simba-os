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
package org.simbasecurity.core.spring.quartz;

import org.quartz.*;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

public class BeanInvokingJobDetailFactoryBean implements FactoryBean<JobDetail>, BeanNameAware, InitializingBean {

    private String name;

    private String group = Scheduler.DEFAULT_GROUP;

    private boolean concurrent = true;

    private String targetBeanName;

    private String executionMethod;

    private String[] jobListenerNames;

    private String beanName;

    private JobDetail jobDetail;

    /**
     * Set the name of the job.
     * <p/>
     * Default is the bean name of this FactoryBean.
     *
     * @see org.quartz.JobDetail#setName
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the group of the job.
     * <p/>
     * Default is the default group of the Scheduler.
     *
     * @see org.quartz.JobDetail#setGroup
     * @see org.quartz.Scheduler#DEFAULT_GROUP
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Specify whether or not multiple jobs should be run in a concurrent
     * fashion. The behavior when one does not want concurrent jobs to be
     * executed is realized through adding the {@link StatefulJob} interface.
     * More information on stateful versus stateless jobs can be found <a
     * href="http://www.opensymphony.com/quartz/tutorial.html#jobsMore"
     * >here</a>.
     * <p/>
     * The default setting is to run jobs concurrently.
     */
    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    /**
     * @param targetBeanName Set the name of the target bean in the Spring BeanFactory.
     */
    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    /**
     * @param executionMethod Set the name of the method to invoke on the bean.
     */
    public void setExecutionMethod(String executionMethod) {
        this.executionMethod = executionMethod;
    }

    /**
     * Set a list of JobListener names for this job, referring to non-global
     * JobListeners registered with the Scheduler.
     * <p/>
     * A JobListener name always refers to the name returned by the JobListener
     * implementation.
     *
     * @see org.springframework.scheduling.quartz.SchedulerFactoryBean#setJobListeners
     * @see org.quartz.JobListener#getName
     */
    public void setJobListenerNames(String[] names) {
        this.jobListenerNames = names;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void afterPropertiesSet() throws ClassNotFoundException, NoSuchMethodException {

        // Use specific name if given, else fall back to bean name.
        String name = (this.name != null ? this.name : this.beanName);

        // Consider the concurrent flag to choose between stateful and stateless
        // job.
        Class<? extends Job> jobClass = (this.concurrent ? BeanInvokingJob.class : StatefullBeanInvokingJob.class);

        this.jobDetail = JobBuilder.newJob(jobClass)
                                   .withIdentity(new JobKey(name, this.group))
                                   .usingJobData("beanName", targetBeanName)
                                   .usingJobData("executionMethod", executionMethod)
                                   .storeDurably()
                                   .build();

        postProcessJobDetail(this.jobDetail);
    }

    /**
     * Callback for post-processing the JobDetail to be exposed by this
     * FactoryBean.
     * <p/>
     * The default implementation is empty. Can be overridden in subclasses.
     *
     * @param jobDetail the JobDetail prepared by this FactoryBean
     */
    protected void postProcessJobDetail(JobDetail jobDetail) {
    }

    public JobDetail getObject() {
        return this.jobDetail;
    }

    public Class<JobDetail> getObjectType() {
        return JobDetail.class;
    }

    public boolean isSingleton() {
        return true;
    }

    /**
     * Quartz Job implementation that invokes a specified method. Automatically
     * applied by MethodInvokingJobDetailFactoryBean.
     */
    @PersistJobDataAfterExecution
    public static class BeanInvokingJob extends QuartzJobBean {

        private static final String APPLICATION_CONTEXT_KEY = "applicationContext";

        private String beanName;
        private String executionMethod;

        /**
         * Invoke the method via the MethodInvoker.
         */
        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            MethodInvoker methodInvoker = new MethodInvoker();

            try {
                methodInvoker.setTargetObject(getBean(context, beanName));
                methodInvoker.setTargetMethod(executionMethod);

                methodInvoker.prepare();

                context.setResult(methodInvoker.invoke());
            } catch (InvocationTargetException ex) {
                if (ex.getTargetException() instanceof JobExecutionException) {
                    throw (JobExecutionException) ex.getTargetException();
                } else {
                    throw new JobMethodInvocationFailedException(methodInvoker, ex.getTargetException());
                }
            } catch (JobExecutionException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new JobExecutionException(ex);
            }
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }

        public void setExecutionMethod(String executionMethod) {
            this.executionMethod = executionMethod;
        }

        protected Object getBean(JobExecutionContext context, String beanName) throws JobExecutionException {
            return getApplicationContext(context).getBean(beanName);
        }

        protected ApplicationContext getApplicationContext(JobExecutionContext context) throws JobExecutionException {
            ApplicationContext appCtx = null;
            try {
                appCtx = (ApplicationContext) context.getScheduler().getContext().get(APPLICATION_CONTEXT_KEY);
            } catch (SchedulerException e) {
                throw new JobExecutionException(e);
            }
            if (appCtx == null) {
                throw new JobExecutionException("No application context available in scheduler context for key \"" + APPLICATION_CONTEXT_KEY + "\"");
            }
            return appCtx;
        }
    }

    /**
     * Extension of the MethodInvokingJob, implementing the StatefulJob
     * interface. Quartz checks whether or not jobs are stateful and if so,
     * won't let jobs interfere with each other.
     */
    @PersistJobDataAfterExecution
    public static class StatefullBeanInvokingJob extends BeanInvokingJob implements StatefulJob {

        // No implementation, just an addition of the tag interface StatefulJob
        // in order to allow stateful method invoking jobs.
    }
}
