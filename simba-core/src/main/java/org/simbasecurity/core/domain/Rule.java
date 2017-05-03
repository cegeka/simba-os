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


/**
 * @since 1.0
 */
public interface Rule extends Versionable {

    /**
     * @return the unique name for the rule.
     */
    String getName();

    /**
     * @return the resource name to which the rule is linked.
     * @see org.simbasecurity.core.domain.URLRule
     * @see org.simbasecurity.core.domain.ResourceRule
     */
    String getResourceName();

    /**
     * @param resource sets the resource name to which the rule is linked
     */
    void setResourceName(String resource);

    Policy getPolicy();

    void setPolicy(Policy policy);

}
