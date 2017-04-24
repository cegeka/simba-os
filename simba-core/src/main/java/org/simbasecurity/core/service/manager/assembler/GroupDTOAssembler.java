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

import org.simbasecurity.core.domain.AbstractVersionedEntity;
import org.simbasecurity.core.domain.Group;
import org.simbasecurity.core.service.manager.dto.GroupDTO;

import java.util.ArrayList;
import java.util.Collection;

import static org.simbasecurity.core.service.manager.assembler.VersionedDTOAssemblerUtil.applyVersionAndId;

public class GroupDTOAssembler {
    private GroupDTOAssembler() {
    }

    public static Collection<GroupDTO> assemble(final Collection<Group> groups) {
        final Collection<GroupDTO> groupDTOS = new ArrayList<GroupDTO>(groups.size());
        for (final Group group : groups) {
            groupDTOS.add(assemble(group));
        }
        return groupDTOS;
    }

    public static GroupDTO assemble(final Group group) {
        final GroupDTO groupDTO = new GroupDTO();
        applyVersionAndId((AbstractVersionedEntity) group, groupDTO);
        groupDTO.setName(group.getName());
        return groupDTO;
    }
}
