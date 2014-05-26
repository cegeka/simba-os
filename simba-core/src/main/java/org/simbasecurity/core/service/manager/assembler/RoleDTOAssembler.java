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
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.service.manager.dto.RoleDTO;

public class RoleDTOAssembler {
    private RoleDTOAssembler() {
        // utility class should not be instantiated
    }

    public static Collection<RoleDTO> assemble(Collection<Role> roles) {
        Collection<RoleDTO> roleDTOs = new ArrayList<RoleDTO>(roles.size());

        for (Role role : roles) {
            roleDTOs.add(assemble(role));
        }

        return roleDTOs;
    }


    public static RoleDTO assemble(Role role) {
        RoleDTO roleDTO = new RoleDTO();
        applyVersionAndId((AbstractVersionedEntity) role, roleDTO);
        roleDTO.setName(role.getName());

        return roleDTO;
    }
}
