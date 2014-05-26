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

import org.simbasecurity.core.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class RoleDatabaseRepository extends AbstractVersionedDatabaseRepository<Role> implements RoleRepository {

    @SuppressWarnings("unchecked")
    public Role findByName(String roleName) {
        Query query = entityManager.createQuery("SELECT r FROM RoleEntity r WHERE r.name = :roleName")
                .setParameter("roleName", roleName);
        List<Role> resultList = query.getResultList();

        if (resultList.size() == 0) {
            return null;
        } else if (resultList.size() == 1) {
            return resultList.get(0);
        }

        throw new IllegalStateException("Multiple roles found for rolename: '" + roleName + "'");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Role> findNotLinked(Policy policy) {
        Query query = entityManager.createQuery("SELECT r FROM RoleEntity r WHERE :policy not in " +
                "elements(r.policies) order by r.name")
                .setParameter("policy", policy);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Role> findNotLinked(User user) {
        Query query = entityManager.createQuery("SELECT r FROM RoleEntity r WHERE :user not in " +
                "elements(r.users) order by r.name")
                .setParameter("user", user);

        return query.getResultList();
    }

    @Override
    public Collection<Role> findNotLinked(Group group) {
        ArrayList<Role> result = new ArrayList<Role>();
        for (Role role : findAll()) {
            if(!role.getGroups().contains(group)) {
                result.add(role);
            }
        }
        return result;
    }


    @Override
    protected Class<? extends Role> getEntityType() {
        return RoleEntity.class;
    }
}
