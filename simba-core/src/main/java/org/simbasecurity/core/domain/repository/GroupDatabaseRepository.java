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
import org.simbasecurity.core.domain.GroupEntity;
import org.simbasecurity.core.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class GroupDatabaseRepository extends AbstractVersionedDatabaseRepository<Group> implements GroupRepository {

    @Override
    protected Class<? extends Group> getEntityType() {
        return GroupEntity.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Group findByCN(String cn) {
        Query query = entityManager.createQuery("SELECT g FROM GroupEntity g WHERE g.cn = :cn")
                                   .setParameter("cn", cn);
        List<Group> resultList = query.getResultList();

        if (resultList.size() == 0) {
            return null;
        } else if (resultList.size() == 1) {
            return resultList.get(0);
        }
        throw new IllegalStateException("Multiple users found for username: '" + cn + "'");
    }

    @Override
    public Collection<Group> find(User user) {
        ArrayList<Group> result = new ArrayList<Group>();
        Query query = entityManager.createQuery("SELECT g FROM GroupEntity g ");
        for (Group group : (List<Group>)query.getResultList()) {
            if(group.getUsers().contains(user)) {
                result.add(group);
            }
        }
        return result;
//        TODO: use implementation below when hibernate bug is fixed
//        Query query = entityManager.createQuery(
//                "SELECT g FROM GroupEntity g " +
//                "WHERE :user in g.users")
//                .setParameter("user", user);
//        return query.getResultList();
    }
}
