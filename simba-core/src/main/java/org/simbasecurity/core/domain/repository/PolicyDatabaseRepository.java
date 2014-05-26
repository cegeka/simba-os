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
public class PolicyDatabaseRepository extends AbstractVersionedDatabaseRepository<Policy> implements PolicyRepository {

    @SuppressWarnings("unchecked")
    public Policy findByName(String policyName) {
        Query query = entityManager.createQuery("SELECT p FROM PolicyEntity p WHERE p.name = :policyName")
                .setParameter("policyName", policyName);
        List<Policy> resultList = query.getResultList();

        if (resultList.size() == 0) {
            return null;
        } else if (resultList.size() == 1) {
            return resultList.get(0);
        }

        throw new IllegalStateException("Multiple policies found for policyname: '" + policyName + "'");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Policy> findNotLinked(Role role) {
        ArrayList<Policy> result = new ArrayList<Policy>();
        Query query = entityManager.createQuery("SELECT p FROM PolicyEntity p ");
        List<Policy> temp = (List<Policy>)query.getResultList();
        for (Policy policy : temp) {
            if(!policy.getRoles().contains(role)) {
                result.add(policy);
            }
        }
        return result;
//        TODO: use implementation below when hibernate bug (HHH-8907) is fixed
//        Query query = entityManager.createQuery(
//                "SELECT p FROM PolicyEntity p " +
//                        "WHERE :role not in " +
//                "(p.roles) order by p.name")
//                .setParameter("role", role);
//        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Policy> find(User user) {
        Query query = entityManager.createQuery(
                "SELECT DISTINCT policy " +
                        "FROM PolicyEntity policy " +
                        "JOIN policy.roles role JOIN role.users user " +
                        "WHERE user=:user");

        query.setParameter("user", user);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Policy find(Rule rule) {
        Query query = entityManager.createQuery("SELECT r.policy FROM RuleEntity r WHERE r = :rule")
                .setParameter("rule", rule);

        List<Policy> optionalResult = query.getResultList();
        return optionalResult.size() > 0 ? optionalResult.get(0) : null;
    }


    @Override
    protected Class<? extends Policy> getEntityType() {
        return PolicyEntity.class;
    }
}
