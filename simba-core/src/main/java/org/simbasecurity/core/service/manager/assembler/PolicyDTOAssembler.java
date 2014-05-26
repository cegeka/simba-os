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
package org.simbasecurity.core.service.manager.assembler;

import static org.simbasecurity.core.service.manager.assembler.VersionedDTOAssemblerUtil.*;

import java.util.ArrayList;
import java.util.Collection;

import org.simbasecurity.core.domain.AbstractVersionedEntity;
import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.service.manager.dto.PolicyDTO;

public final class PolicyDTOAssembler {
    private PolicyDTOAssembler() {
    }

    public static Collection<PolicyDTO> assemble(final Collection<Policy> policies) {
        final Collection<PolicyDTO> policyDTOs = new ArrayList<PolicyDTO>(policies.size());
        for (final Policy policy : policies) {
            policyDTOs.add(assemble(policy));
        }
        return policyDTOs;
    }

    public static PolicyDTO assemble(final Policy policy) {
        final PolicyDTO policyDTO = new PolicyDTO();
        applyVersionAndId((AbstractVersionedEntity) policy, policyDTO);
        policyDTO.setName(policy.getName());
        return policyDTO;
    }
}
