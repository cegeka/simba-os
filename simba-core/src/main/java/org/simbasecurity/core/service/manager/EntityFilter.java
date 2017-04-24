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

package org.simbasecurity.core.service.manager;

import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.User;

import java.util.function.Predicate;

/**
 * This class allows the injection of custom filters in the {@link EntityFilterService}.
 * <p/>
 * If an EntityFilter should not require filtering on a specific entity, it can use:
 * <code>
 *     e -> true
 * </code>
 * as predicate to allow all entities to remain in the collection.
 *
 * @since 3.0.0
 */
public interface EntityFilter {

    /**
     * @return the predicate to use for filtering roles
     */
    Predicate<Role> rolePredicate();

    /**
     * @return the predicate to use for filtering policies
     */
    Predicate<Policy> policyPredicate();

    /**
     * @return the predicate to use for filtering users
     */
    Predicate<User> userPredicate();
}
