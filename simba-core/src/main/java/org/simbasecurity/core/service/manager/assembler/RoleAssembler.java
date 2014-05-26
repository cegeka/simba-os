package org.simbasecurity.core.service.manager.assembler;

import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.RoleEntity;

public class RoleAssembler {
    public static Role createRole(String name) {
        return new RoleEntity(name);
    }
}
