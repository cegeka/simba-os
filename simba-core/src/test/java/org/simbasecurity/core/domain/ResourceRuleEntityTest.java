/*
 * Copyright 2013 Simba Open Source
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
package org.simbasecurity.core.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResourceRuleEntityTest {

    private static final String NAME = "rule";
    private ResourceRuleEntity resourceRuleEntity;

    @Before
    public void setUp() {
        resourceRuleEntity = new ResourceRuleEntity(NAME);
    }

    @Test
    public void isAllowedUnknownAlwaysReturnsFalse() {
        assertFalse(resourceRuleEntity.isAllowed(ResourceOperationType.UNKNOWN));
    }

    @Test
    public void isAllowed_operationNotAllowed() {
        assertFalse(resourceRuleEntity.isAllowed(ResourceOperationType.CREATE));
    }

    @Test
    public void isAllowed_operationAllowed() {
        resourceRuleEntity.setCreateAllowed(true);

        assertTrue(resourceRuleEntity.isAllowed(ResourceOperationType.CREATE));
    }

}
