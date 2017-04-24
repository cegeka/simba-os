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

import java.util.Collection;
import java.util.Set;

/**
 * @since 1.0
 */
public interface Role extends Versionable {

    /**
     * @return the unique name for the role.
     */
    String getName();

    /**
     * @return the collection of {@link org.simbasecurity.core.domain.User users} having this role.
     */
    Set<User> getUsers();

    /**
     * @return the collection of {@link org.simbasecurity.core.domain.Policy policies} linked to the role.
     */
    Set<Policy> getPolicies();

    Collection<Group> getGroups();

    void addPolicy(Policy policy);

    void removePolicy(Policy policy);

    void addPolicies(Collection<Policy> policies);

    void removeUser(User user);

    void addUser(User user);

    void addUsers(Collection<User> users);


}
