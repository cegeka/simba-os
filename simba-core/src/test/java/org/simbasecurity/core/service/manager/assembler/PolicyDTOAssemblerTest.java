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
package org.simbasecurity.core.service.manager.assembler;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.PolicyEntity;
import org.simbasecurity.core.service.manager.dto.PolicyDTO;

public class PolicyDTOAssemblerTest {

    @Test
    public void testAssembleSinglePolicy() {
        Policy policy = new PolicyEntity("policy name");

        PolicyDTO policyData = PolicyDTOAssembler.assemble(policy);

        assertNotNull(policyData);
        assertEquals(0, policyData.getId());
        assertEquals(policy.getName(), policyData.getName());

        assertEquals(0, policyData.getId());
        assertEquals(0, policyData.getVersion());
    }

    @Test
    public void testAssembleMultiplePolicies() {
        Policy policy = new PolicyEntity("policy name");

        Collection<PolicyDTO> policyDataList = PolicyDTOAssembler.assemble(Arrays.asList(policy));

        assertNotNull(policyDataList);
        assertEquals(1, policyDataList.size());

    }

}
