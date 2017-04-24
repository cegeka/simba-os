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

import org.simbasecurity.core.service.AuthorizationRequestContext;

/**
 * @since 1.0
 */
public interface Policy extends Versionable {

    /**
     * @return the unique name for the policy.
     */
    String getName();

    /**
     * @return the collection of {@link Rule rules}
     *         for this policy.
     */
    Set<Rule> getRules();

    /**
     * @param rule adds a rule to the policy
     */
    void addRule(Rule rule);

    /**
     * @param rules adds a collections of rules to the policy
     */
    void addRules(Collection<Rule> rules);

    /**
     * @param rule removes a rule from the policy
     */
    void removeRule(Rule rule);

    /**
     * @return the collection of {@link Role roles}
     *         for this policy.
     */
    Set<Role> getRoles();

    /**
     * @param role add a role to the policy
     */
    void addRole(Role role);

    /**
     * @param roles adds a collection of roles to the policy
     */
    void addRoles(Collection<Role> roles);

    /**
     * @param role removes a specific role from the policy
     */
    void removeRole(Role role);

    /**
     * A set of conditions can be configured for a policy. At least one of these
     * conditions has to be met before the policy becomes valid.
     * <p/>
     * If there is no condition added to the policy, then the policy is always
     * valid.
     *
     * @param conditions set the set of conditions for this policy
     */
    void setConditions(Set<Condition> conditions);

    /**
     * @return the set of conditions configured to this policy
     */
    Set<Condition> getConditions();

    /**
     * @param context the request context for the authorization
     * @return <code>true</code> if the policy applies regarding the conditions
     *         set on the policy; <code>false</code> otherwise
     * @see #setConditions(java.util.Set)
     */
    boolean applies(AuthorizationRequestContext context);

    /**
     * @return
     */
    long getExpirationTimestamp(AuthorizationRequestContext context);
}