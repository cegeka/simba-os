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
