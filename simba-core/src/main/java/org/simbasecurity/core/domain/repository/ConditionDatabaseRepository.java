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
import javax.persistence.Query;

import org.simbasecurity.core.domain.Condition;
import org.simbasecurity.core.domain.ConditionEntity;
import org.simbasecurity.core.domain.Policy;
import org.springframework.stereotype.Repository;

@Repository
public class ConditionDatabaseRepository extends AbstractVersionedDatabaseRepository<Condition> implements ConditionRepository {

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Policy> findPolicies(Condition condition) {
        Query query = entityManager.createQuery("SELECT policy FROM PolicyEntity policy WHERE :condition in elements(policy.conditions) ");
        query.setParameter("condition", condition);
        return new ArrayList<Policy>(query.getResultList());
    }

    @Override
    public void updatePolicies(Condition condition, Collection<Policy> newPolicies) {
        Collection<Policy> oldPolicies = findPolicies(condition);
        for (Policy policy : oldPolicies) {
            if (!newPolicies.remove(policy)) {
                policy.getConditions().remove(condition);
            }
        }

        for (Policy policy : newPolicies) {
            policy.getConditions().add(condition);
        }
    }

    @Override
    protected Class<? extends Condition> getEntityType() {
        return ConditionEntity.class;
    }
}
