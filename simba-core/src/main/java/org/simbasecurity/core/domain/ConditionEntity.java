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

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.simbasecurity.core.service.AuthorizationRequestContext;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "SIMBA_CONDITION")
public abstract class ConditionEntity extends AbstractVersionedEntity implements Condition {

    private static final long serialVersionUID = -723660543552695800L;

    @Id
    @GeneratedValue(generator = "simbaSequence", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_CONDITION")
    protected long id = 0;

    private String name;

    @ManyToMany(targetEntity = UserEntity.class, cascade = {
        CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "SIMBA_CONDITION_USER", joinColumns = @JoinColumn(name = "CONDITION_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
    private Set<User> exemptedUsers;

    public ConditionEntity() {
    }

    protected ConditionEntity(String name) {
        this.name = name;
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
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<User> getExemptedUsers() {
        return exemptedUsers;
    }

    @Override
    public void setExemptedUsers(Set<User> exemptedUsers) {
        this.exemptedUsers = exemptedUsers;
    }

    @Override
    public final boolean applies(AuthorizationRequestContext context) {
        for (User user : getExemptedUsers()) {
            if (user.getUserName().equals(context.getUsername())) {
                return true;
            }
        }

        return conditionApplies(context);
    }

    protected abstract boolean conditionApplies(AuthorizationRequestContext context);

    /**
     * This default implementation sets the expiration timestamp for a condition
     * to never expire ({@link Long#MAX_VALUE}). Subclasses should override this
     * if they can give a more meaningful expiration timestamp.
     * 
     * @return the default expiration timestamp for a condition.
     */
    @Override
    public long getExpirationTimestamp(AuthorizationRequestContext context) {
        return Long.MAX_VALUE;
    }

}
