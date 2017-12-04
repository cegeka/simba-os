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
package org.simbasecurity.core.domain.repository;

import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.user.EmailAddress;

import java.util.Collection;

public interface UserRepository extends AbstractVersionedRepository<User> {

    /**
     * @return all stored users
     */
    @Override
    Collection<User> findAll();

    /**
     * @param userName the user name
     * @return the user specified by given user name
     */
    User findByName(String userName);

    Collection<User> findNotLinked(Role role);

    Collection<User> findForRole(Role role);

    Collection<User> findAllOrderedByName();

    Collection<User> searchUsersOrderedByName(String searchText);

    User refreshWithOptimisticLocking(String username, int version);

    User findByEmail(EmailAddress emailAddress);
}
