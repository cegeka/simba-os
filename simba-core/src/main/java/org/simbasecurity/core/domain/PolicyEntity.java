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
package org.simbasecurity.core.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.simbasecurity.core.service.AuthorizationRequestContext;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SIMBA_POLICY")
public class PolicyEntity extends AbstractVersionedEntity implements Policy {

    private static final long serialVersionUID = 552484022516217422L;

    @Id
    @GeneratedValue(generator = "simbaSequence", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_POLICY")
    protected long id = 0;

    @Column(unique = true)
    private String name;

    @ManyToMany(targetEntity = RoleEntity.class, cascade = {
        CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "SIMBA_POLICY_SIMBA_ROLE", joinColumns = @JoinColumn(name = "POLICY_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    @OrderBy("name")
    private Set<Role> roles;

    @OneToMany(targetEntity = RuleEntity.class, cascade = CascadeType.ALL, mappedBy = "policy")
    @OrderBy("name")
    private Set<Rule> rules;

    @ManyToMany(targetEntity = ConditionEntity.class, cascade = {
        CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "SIMBA_POLICY_CONDITION", joinColumns = @JoinColumn(name = "POLICY_ID"), inverseJoinColumns = @JoinColumn(name = "CONDITION_ID"))
    private Set<Condition> conditions;

    public PolicyEntity() {
    }

    public PolicyEntity(String name) {
        this.name = name;
        roles = new HashSet<Role>();
        rules = new HashSet<Rule>();
        conditions = new HashSet<Condition>();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<Role> getRoles() {
        if (roles == null) {
            roles = new HashSet<Role>();
        }
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public void addRoles(Collection<Role> newRoles) {
        for (Role role : newRoles) {
            addRole(role);
        }
    }

    @Override
    public void addRole(Role role) {
        roles.add(role);
        role.getPolicies().add(this);
    }

    @Override
    public Set<Rule> getRules() {
        return rules;
    }

    @Override
    public void addRules(Collection<Rule> rules) {
        for (Rule rule : rules) {
            addRule(rule);
        }
    }

    @Override
    public void addRule(Rule rule) {
        this.rules.add(rule);
        rule.setPolicy(this);
    }

    @Override
    public void removeRule(Rule rule) {
        rules.remove(rule);
        rule.setPolicy(null);
    }

    @Override
    public void removeRole(Role role) {
        roles.remove(role);
        role.removePolicy(this);
    }

    @Override
    public Set<Condition> getConditions() {
        return conditions;
    }

    @Override
    public void setConditions(Set<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean applies(AuthorizationRequestContext context) {
        for (Condition condition : conditions) {
            if (!condition.applies(context)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long getExpirationTimestamp(AuthorizationRequestContext context) {
        long timestamp = Long.MAX_VALUE;

        for (Condition condition : conditions) {
            if (!condition.applies(context)) {
                timestamp = Math.min(timestamp, condition.getExpirationTimestamp(context));
            }
        }
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PolicyEntity)) {
            return false;
        }
        PolicyEntity pe = (PolicyEntity) o;
        return new EqualsBuilder().append(id, pe.id).append(name, pe.name)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(name).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("id", id).append("name", name).toString();
    }

    @PreRemove
    protected void cleanupBeforeDelete() {
        ArrayList<Role> rolesCopy = new ArrayList<Role>(roles);
        for (Role role : rolesCopy) {
            role.removePolicy(this);
        }
        roles.clear();
    }

}
