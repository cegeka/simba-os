package org.simbasecurity.core.service.manager.assembler;

import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.PolicyEntity;

public class PolicyAssembler {
    public static Policy createPolicy(String name) {
        return new PolicyEntity(name);
    }
}
