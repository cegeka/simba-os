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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.service.AuthorizationRequestContext;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class PolicyEntityTest {

    @org.junit.Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private AuthorizationRequestContext context;

    private PolicyEntity policyEntity;

    @Before
    public void setup() {
        this.policyEntity = new PolicyEntity("test");
    }

    @After
    public void verify() {
        verifyNoMoreInteractions(context);
    }

    @Test
    public void policyAppliesWhenNoConditions() {
        assertTrue(policyEntity.applies(context));
    }

    @Test
    public void policyDoesNotApplyWhenNotAllConditionsAreMet() {
        Condition validCondition = mock(Condition.class);
        when(validCondition.applies(context)).thenReturn(true);

        Condition invalidCondition = mock(Condition.class);
        when(invalidCondition.applies(context)).thenReturn(false);

        Set<Condition> conditions = new HashSet<Condition>();
        conditions.add(validCondition);
        conditions.add(invalidCondition);

        policyEntity.setConditions(conditions);

        assertFalse(policyEntity.applies(context));
    }

    @Test
    public void policyDoesApplyWhenAllConditionsAreMet() {
        Condition validCondition = mock(Condition.class);
        when(validCondition.applies(context)).thenReturn(true);

        Condition otherValidCondition = mock(Condition.class);
        when(otherValidCondition.applies(context)).thenReturn(true);

        Set<Condition> conditions = new HashSet<Condition>();
        conditions.add(validCondition);
        conditions.add(otherValidCondition);

        policyEntity.setConditions(conditions);

        assertTrue(policyEntity.applies(context));
    }
}
