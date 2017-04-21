/*
 * Copyright 2013 Simba Open Source
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
package org.simbasecurity.core.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "SIMBA_RULE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "RULE_TYPE")
public abstract class RuleEntity extends AbstractVersionedEntity implements Rule {

    private static final long serialVersionUID = -7300017447485361764L;

    @Id
    @GeneratedValue(generator = "simbaSequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_RULE")
    protected long id = 0;

    @Column(unique = true)
    private String name;

    private String resourceName;

    @ManyToOne(targetEntity = PolicyEntity.class)
    private Policy policy;

    public RuleEntity() {
    }

    protected RuleEntity(String name) {
        this.name = name;
        this.resourceName = name;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public void setResourceName(String resource) {
        this.resourceName = resource;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RuleEntity)) {
            return false;
        }
        RuleEntity re = (RuleEntity) o;
        return new EqualsBuilder()
            .append(id, re.id)
            .append(name, re.name)
            .append(resourceName, re.resourceName)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
            append(id).
            append(name).
            append(resourceName).
            toHashCode();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public Policy getPolicy() {
        return policy;
    }

    @Override
    public void setPolicy(Policy newPolicy) {
        if (policy != null && newPolicy == null) {
            Policy oldPolicy = policy;
            policy = null;
            oldPolicy.removeRule(this);
        }

        if (newPolicy != null && !newPolicy.equals(this.policy)) {
            this.policy = newPolicy;
            newPolicy.addRule(this);
        }
    }
}
