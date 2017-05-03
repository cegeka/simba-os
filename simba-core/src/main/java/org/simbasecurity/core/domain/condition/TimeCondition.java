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
package org.simbasecurity.core.domain.condition;

import java.text.ParseException;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.AccessType;
import org.simbasecurity.core.domain.ConditionEntity;
import org.simbasecurity.core.service.AuthorizationRequestContext;
import org.simbasecurity.core.spring.quartz.ExtendedCronExpression;

/**
 * Represents a sequence of time intervals. The condition is satisfied if the
 * current time is inside any of intervals. The sequence is defined by two cron
 * expressions - startCondition and endCondition. startCondition is the sequence
 * of time moments in which the intervals start. endCondition is the sequence of
 * time moments in which the intervals end. So, for this definition to be
 * correct it is required that for every start moment it should be an end moment
 * in such a way that intervals don't overlap. Right now the rule above is not
 * validated.
 * <p/>
 * Any valid CRON expressions are supported. See <a
 * href="http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html">Quartz
 * expressions help</a> for a complete guide.
 */
@Entity
@Table(name = "SIMBA_TIME_CONDITION")
@PrimaryKeyJoinColumn(name = "CONDITION_ID")
@AccessType("property")
public class TimeCondition extends ConditionEntity {

    private static final long serialVersionUID = 1812282413540572395L;

    private String startCondition;
    private String endCondition;

    @Transient
    private ExtendedCronExpression startExpression;

    @Transient
    private ExtendedCronExpression endExpression;

    public TimeCondition() {
    }

    public TimeCondition(String startCondition, String endCondition) {
        this.setStartCondition(startCondition);
        this.setEndCondition(endCondition);
    }

    @Column(name = "START_EXPR")
    public String getStartCondition() {
        return startCondition;
    }

    public void setStartCondition(String startCondition) {
        try {
            this.startExpression = new ExtendedCronExpression(startCondition);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        this.startCondition = startCondition;
    }

    @Column(name = "END_EXPR")
    public String getEndCondition() {
        return endCondition;
    }

    public void setEndCondition(String endCondition) {
        try {
            this.endExpression = new ExtendedCronExpression(endCondition);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        this.endCondition = endCondition;
    }

    @Override
    public boolean conditionApplies(AuthorizationRequestContext context) {
        return isInInterval(context.getTime());
    }

    boolean isInInterval(long timestamp) {
        Date checkDate = new Date(timestamp);

        Date beforeDate = startExpression.getTimeBefore(checkDate);
        Date afterDate = endExpression.getTimeAfter(beforeDate);

        return beforeDate != null && afterDate != null
                && !checkDate.before(beforeDate) && !checkDate.after(afterDate);
    }

    @Override
    public long getExpirationTimestamp(AuthorizationRequestContext context) {
        return getExpirationTimestamp(context.getTime());
    }

    long getExpirationTimestamp(long timestamp) {
        ExtendedCronExpression expr = isInInterval(timestamp) ? endExpression : startExpression;

        Date oneMillisecondBeforeTimestamp = new Date(timestamp - 1);

        return expr.getTimeAfter(oneMillisecondBeforeTimestamp).getTime();
    }

}
