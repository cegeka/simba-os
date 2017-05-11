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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The EntityFilterService is used by the manager services to filter entity lists before returning them to the
 * manager user interface. This allows hiding certain entities from certain users.
 * <p/>
 * When no {@link EntityFilter}'s are configured in the Spring context, no filtering to the entity collections is applied.
 * Multiple {@link EntityFilter} predicates are combined using the <code>and</code> operator.
 *
 * @see UserManagerService
 * @see PolicyManagerService
 * @see RoleManagerService
 * @see EntityFilter
 *
 * @since 3.0.0
 */
@Service
public class EntityFilterService {

    private List<EntityFilter> filters;

    @Autowired
    public EntityFilterService(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<List<EntityFilter>> filters) {
        this.filters = filters.orElseGet(ArrayList::new);
    }

    public Collection<Role> filterRoles(Collection<Role> input) {
        Collection<Role> result = input;
        for (EntityFilter filter : filters) {
            result = filter.filterRoles(result);
        }
        return result;
    }

    public Collection<Policy> filterPolicies(Collection<Policy> input) {
        Collection<Policy> result = input;
        for (EntityFilter filter : filters) {
            result = filter.filterPolicies(result);
        }
        return result;
    }

    public Collection<User> filterUsers(Collection<User> input) {
        Collection<User> result = input;
        for (EntityFilter filter : filters) {
            result = filter.filterUsers(result);
        }
        return result;
    }
}
