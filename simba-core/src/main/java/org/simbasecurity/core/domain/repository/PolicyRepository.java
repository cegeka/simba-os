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

import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.Rule;
import org.simbasecurity.core.domain.User;

public interface PolicyRepository extends AbstractVersionedRepository<Policy> {

    /**
     * @return all stored roles
     */
    Collection<Policy> findAll();

    /**
     * @param policyName the policy name
     * @return the policy specified by given policy name
     */
    Policy findByName(String policyName);

    Policy find(Rule rule);

    Collection<Policy> findNotLinked(Role role);

    Collection<Policy> findForRole(Role role);

    /**
     * @param policy the policy
     * @return the list of policies specified by given user
     */
    Collection<Policy> find(User user);
}
