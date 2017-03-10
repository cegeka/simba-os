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
package org.simbasecurity.core.domain.repository;

import org.simbasecurity.core.domain.Group;
import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.User;

import java.util.Collection;

public interface RoleRepository extends AbstractVersionedRepository<Role> {

    /**
     * @return all stored roles
     */
    Collection<Role> findAll();

    /**
     * @param roleName the role name
     * @return the role specified by given role name
     */
    Role findByName(String roleName);

    Collection<Role> findNotLinked(Policy policy);

    Collection<Role> findForPolicy(Policy policy);

    Collection<Role> findNotLinked(User user);

    Collection<Role> findForUser(User user);

    Collection<Role> findNotLinked(Group group);
}