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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.RoleEntity;
import org.simbasecurity.core.service.manager.dto.RoleDTO;

public class RoleDTOAssemblerTest {

    @Test
    public void testAssembleSingleRole() {
        Role role = new RoleEntity("role name");

        RoleDTO roleData = RoleDTOAssembler.assemble(role);

        assertNotNull(roleData);
        assertEquals(0, roleData.getId());
        assertEquals(role.getName(), roleData.getName());

        assertEquals(0, role.getId());
        assertEquals(0, role.getVersion());
    }

    @Test
    public void testAssembleMultipleRoles() {
        Role role = new RoleEntity("role name");

        Collection<RoleDTO> roleDataList = RoleDTOAssembler.assemble(Arrays.asList(role));

        assertNotNull(roleDataList);
        assertEquals(1, roleDataList.size());

    }

}
