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
package org.simbasecurity.core.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SIMBA_ROLE")
public class RoleEntity extends AbstractVersionedEntity implements Role {

    private static final long serialVersionUID = 552484022516217422L;

    @Id
    @GeneratedValue(generator = "simbaSequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_ROLE", allocationSize = 1)
    protected long id = 0;

    @Column(unique = true)
    private String name;

    @ManyToMany(targetEntity = PolicyEntity.class, mappedBy = "roles")
    @OrderBy("name")
    private Set<Policy> policies = new HashSet<Policy>();

    @ManyToMany(targetEntity = UserEntity.class, mappedBy = "roles")
    @OrderBy("userName")
    private Set<User> users = new HashSet<User>();

    @ManyToMany(targetEntity = GroupEntity.class, mappedBy = "roles")
    @OrderBy("name")
    private Set<Group> groups = new HashSet<Group>();

    public RoleEntity() {
    }

    public RoleEntity(String name) {
        if(StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("error.role.name.blank");
        }
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
    public Set<User> getUsers() {
        return users;
    }

    @Override
    public Set<Policy> getPolicies() {
        return policies;
    }

    @Override
    public Collection<Group> getGroups() {
        return groups;
    }

    @Override
    public void addPolicies(Collection<Policy> newPolicies) {
        for (Policy policy : newPolicies) {
            addPolicy(policy);
        }
    }

    @Override
    public void addPolicy(Policy policy) {
        policies.add(policy);
        policy.getRoles().add(this);
    }

    @Override
    public void addUser(User user) {
        users.add(user);
        user.getRoles().add(this);
    }

    @Override
    public void addUsers(Collection<User> newUsers) {
        for (User user : newUsers) {
            addUser(user);
        }
    }

    @Override
    public void removePolicy(Policy policy) {
        policies.remove(policy);
        policy.getRoles().remove(this);
    }

    @Override
    public void removeUser(User user) {
        users.remove(user);
        user.getRoles().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoleEntity)) {
            return false;
        }
        RoleEntity re = (RoleEntity) o;
        return new EqualsBuilder()
            .append(id, re.id)
            .append(name, re.name)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
            append(id).
            append(name).
            toHashCode();
    }

    public void addGroup(GroupEntity group) {
        groups.add(group);
    }

    void removeGroup(GroupEntity groupEntity) {
        groups.remove(groupEntity);
        groupEntity.getRoles().remove(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id).append("name", name).toString();
    }

    @PreRemove
    protected void cleanupBeforeDelete() {
        cleanupPolicies();
        cleanupGroups();
        cleanupUsers();
    }

    private void cleanupGroups() {
        ArrayList<Group> groupsCopy = new ArrayList(groups);
        for (Group group : groupsCopy) {
            group.removeRole(this);
        }
        groups.clear();
    }

    private void cleanupUsers() {
        ArrayList<User> usersCopy = new ArrayList(users);
        for (User user : usersCopy) {
            user.removeRole(this);
        }
        users.clear();
    }

    private void cleanupPolicies() {
        ArrayList<Policy> policiesCopy = new ArrayList(policies);
        for (Policy policy : policiesCopy) {
            policy.removeRole(this);
        }
        policies.clear();
    }
}
