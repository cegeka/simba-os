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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Query;

import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UserDatabaseRepository extends AbstractVersionedDatabaseRepository<User> implements UserRepository {

    @Override
    @SuppressWarnings("unchecked")
    public User findByName(String userName) {
        Query query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.userName = :userName").setParameter(
                "userName", userName);
        List<User> resultList = query.getResultList();

        if (resultList.size() == 0) {
            return null;
        } else if (resultList.size() == 1) {
            return resultList.get(0);
        }

        throw new IllegalStateException("Multiple users found for username: '" + userName + "'");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<User> findNotLinked(Role role) {
        Query query = entityManager
                .createQuery("SELECT user FROM UserEntity user WHERE :role not in "
                        + "elements(user.roles) order by user.userName");
        query.setParameter("role", role);
        return new ArrayList<User>(query.getResultList());
    }

    @Override
    protected Class<? extends User> getEntityType() {
        return UserEntity.class;
    }


}
