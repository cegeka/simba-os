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

import java.util.Collection;

import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.ResourceRule;
import org.simbasecurity.core.domain.Rule;
import org.simbasecurity.core.domain.URLRule;

public interface RuleRepository extends AbstractVersionedRepository<Rule> {

    Collection<Rule> findNotLinked(Policy policy);

    Collection<ResourceRule> findResourceRules(String username, String resource);

    Collection<URLRule> findURLRules(String username);

}
