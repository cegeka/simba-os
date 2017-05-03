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

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SIMBA_GROUP")
public class GroupEntity extends AbstractVersionedEntity implements Group {

    private static final long serialVersionUID = 552484022516217422L;

    @Id
    @GeneratedValue(generator = "simbaSequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_GROUP")
    protected long id = 0;

    @ManyToMany(targetEntity = UserEntity.class, mappedBy = "groups")
    @OrderBy("userName")
    private Set<User> users = new HashSet<User>();

    @ManyToMany(targetEntity = RoleEntity.class)
    @JoinTable(name = "SIMBA_GROUP_ROLE", joinColumns = @JoinColumn(name = "GROUP_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    @OrderBy("name")
    private Set<Role> roles = new HashSet<Role>();

    private String name;
    private String cn;

    public GroupEntity() {}

    public GroupEntity(String name, String cn) {
        setName(name);
        setCn(cn);
    }

    void addUser(UserEntity userEntity) {
        users.add(userEntity);
    }

    @Override
    public Collection<Role> getRoles() {
        return roles;
    }

    @Override
    public Collection<User> getUsers() {
        return users;
    }

    @Override
    public void addRole(Role role) {
        roles.add(role);
        ((RoleEntity)role).addGroup(this);
    }

    @Override
    public void addRoles(Collection<Role> roles) {
        for (Role role : roles) {
           addRole(role);
        }
    }

    @Override
    public void removeRole(Role role) {
        roles.remove(role);
        ((RoleEntity)role).removeGroup(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getCN() {
        return cn;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }
}
