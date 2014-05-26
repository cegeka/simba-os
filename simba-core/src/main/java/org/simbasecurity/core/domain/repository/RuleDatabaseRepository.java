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

import java.util.Collection;
import javax.persistence.Query;

import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.ResourceRule;
import org.simbasecurity.core.domain.Rule;
import org.simbasecurity.core.domain.RuleEntity;
import org.simbasecurity.core.domain.URLRule;
import org.springframework.stereotype.Repository;

@Repository
public class RuleDatabaseRepository extends AbstractVersionedDatabaseRepository<Rule> implements RuleRepository {

    private static final String RESOURCE = "resource";

    private static final String POLICY = "policy";

    private static final String USERNAME = "username";

    private static final String QUERY_RESOURCE_RULES_FOR_USER =
            "SELECT rule " +
            "FROM ResourceRuleEntity rule " +
            "JOIN rule.policy policy " +
            "JOIN policy.roles role " +
            "JOIN role.users user " +
            "WHERE user.userName =:username " +
            "AND lower(rule.resourceName) = :resource ";

    private static final String QUERY_RESOURCE_RULES_FOR_GROUPUSER =
            "SELECT rule " +
            "FROM ResourceRuleEntity rule " +
            "JOIN rule.policy policy " +
            "JOIN policy.roles role " +
            "JOIN role.groups g " +
            "JOIN g.users user " +
            "WHERE user.userName=:username " +
            "AND lower(rule.resourceName) = :resource ";

    private static final String QUERY_URL_RULES_FOR_USER =
            "SELECT rule " +
            "FROM URLRuleEntity rule " +
            "JOIN rule.policy policy JOIN policy.roles role JOIN role.users user " +
            "WHERE user.userName =:username";

    private static final String QUERY_URL_RULES_FOR_GROUPUSER =
            "SELECT rule " +
            "FROM URLRuleEntity rule " +
            "JOIN rule.policy policy " +
            "JOIN policy.roles role " +
            "JOIN role.groups g " +
            "JOIN g.users user " +
            "WHERE user.userName =:username";


    public RuleDatabaseRepository() {

    }

    public Collection<ResourceRule> findResourceRules(String username, String resource) {
        Collection<ResourceRule> resourceRules = getResourceRulesDirectly(username, resource);
        resourceRules.addAll(getResourceRulesViaGroups(username, resource));
        return resourceRules;
    }

    @SuppressWarnings("unchecked")
    Collection<ResourceRule> getResourceRulesViaGroups(String username, String resource) {
        Query query = entityManager.createQuery(QUERY_RESOURCE_RULES_FOR_GROUPUSER);

        query.setParameter(USERNAME, username)
             .setParameter(RESOURCE, resource.toLowerCase());

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    Collection<ResourceRule> getResourceRulesDirectly(String username, String resource) {
        Query query = entityManager.createQuery(QUERY_RESOURCE_RULES_FOR_USER);

        query.setParameter(USERNAME, username)
             .setParameter(RESOURCE, resource.toLowerCase());

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Collection<URLRule> findURLRules(String username) {
        Query query1 = entityManager.createQuery(QUERY_URL_RULES_FOR_USER).setParameter(USERNAME, username);
        Collection<URLRule> result = query1.getResultList();
        Query query = entityManager.createQuery(QUERY_URL_RULES_FOR_GROUPUSER).setParameter(USERNAME, username);
        result.addAll(query.getResultList());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Rule> findNotLinked(Policy policy) {
        Query query = entityManager.createQuery(
                "SELECT r FROM RuleEntity r " +
                "WHERE :policy != r.policy " +
                "OR r.policy = null order by r.name")
        .setParameter(POLICY, policy);

        return query.getResultList();
    }

    @Override
    protected Class<? extends Rule> getEntityType() {
        return RuleEntity.class;
    }
}
