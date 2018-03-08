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
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserEntity;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDatabaseRepository extends AbstractVersionedDatabaseRepository<User> implements UserRepository {

    @Override
    public User findByName(String userName) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.userName = :userName", User.class)
                .setParameter("userName", userName);

        List<User> resultList = query.getResultList();

        if (resultList.size() == 0) {
            return null;
        } else if (resultList.size() == 1) {
            return resultList.get(0);
        }

        throw new IllegalStateException("Multiple users found for username: '" + userName + "'");
    }

    @Override
    public Collection<User> findNotLinked(Role role) {
        TypedQuery<User> query = entityManager.createQuery("SELECT user FROM UserEntity user WHERE :role not in elements(user.roles) order by user.userName", User.class)
                .setParameter("role", role);
        return new ArrayList<>(query.getResultList());
    }

    @Override
    public Collection<User> findForRole(Role role) {
        TypedQuery<User> query = entityManager.createQuery("SELECT user FROM UserEntity user WHERE :role in elements(user.roles) order by user.userName", User.class)
                .setParameter("role", role);
        return new ArrayList<>(query.getResultList());
    }

    @Override
    public Collection<User> findAllOrderedByName() {
        TypedQuery<User> query = entityManager.createQuery("SELECT user FROM UserEntity user order by user.userName", User.class);
        return new ArrayList<>(query.getResultList());
    }

    @Override
    public Collection<User> searchUsersOrderedByName(String searchText) {
        TypedQuery<User> query = entityManager.createQuery("SELECT user FROM UserEntity user WHERE UPPER(user.userName) like UPPER(:searchText) or UPPER(user.name) like UPPER(:searchText) or UPPER(user.firstName) like UPPER(:searchText) " +
                "order by user.userName", User.class)
                .setParameter("searchText", "%" + searchText + "%");
        return new ArrayList<>(query.getResultList());
    }

    @Override
    public User refreshWithOptimisticLocking(String username, int version) {
        User user = findByName(username);
        checkOptimisticLocking(user, version);
        return user;
    }

    @Override
    public User findByEmail(EmailAddress email) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM UserEntity u WHERE lower(u.email) = :email AND (u.status =:active or  u.status=:blocked)", User.class)
                .setParameter("email", email.getLowerCaseEmailAddress())
                .setParameter("active", Status.ACTIVE)
                .setParameter("blocked", Status.BLOCKED);

        if (query.getResultList() != null) {
            List<User> resultList = query.getResultList();
            if (resultList.size() == 0) {
                return null;
            } else if (resultList.size() == 1) {
                return resultList.get(0);
            }
            throw new IllegalStateException("Multiple users found for email: '" + email + "'");
        }
        return null;


    }

    @Override
    public Optional<User> findById(long id) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.id = :id", User.class)
                .setParameter("id", id);

        return query.getResultList().stream().findFirst();
    }

    @Override
    protected Class<? extends User> getEntityType() {
        return UserEntity.class;
    }


}
