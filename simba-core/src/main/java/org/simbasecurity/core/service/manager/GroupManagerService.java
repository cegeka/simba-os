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
package org.simbasecurity.core.service.manager;

import org.simbasecurity.core.domain.Group;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.repository.GroupRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.service.manager.assembler.GroupDTOAssembler;
import org.simbasecurity.core.service.manager.assembler.RoleDTOAssembler;
import org.simbasecurity.core.service.manager.assembler.UserDTOAssembler;
import org.simbasecurity.core.service.manager.dto.GroupDTO;
import org.simbasecurity.core.service.manager.dto.RoleDTO;
import org.simbasecurity.core.service.manager.dto.UserDTO;
import org.simbasecurity.core.service.manager.web.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;

@Transactional
@Controller
@RequestMapping("group")
public class GroupManagerService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RoleRepository roleRepository;


    @RequestMapping("findAll")
    @ResponseBody
    public Collection<GroupDTO> findAll() {
        return GroupDTOAssembler.assemble(groupRepository.findAll());
    }

    @RequestMapping("findRoles")
    @ResponseBody
    public Collection<RoleDTO> findRoles(@RequestBody GroupDTO group) {
        return RoleDTOAssembler.assemble(groupRepository.lookUp(group).getRoles());
    }

    @RequestMapping("findRolesNotLinked")
    @ResponseBody
    public Collection<RoleDTO> findRolesNotLinked(@RequestBody GroupDTO group) {
        return RoleDTOAssembler.assemble(roleRepository.findNotLinked(groupRepository.lookUp(group)));
    }

    @RequestMapping("findUsers")
    @ResponseBody
    public Collection<UserDTO> findUsers(@RequestBody GroupDTO group) {
        return UserDTOAssembler.assemble(groupRepository.lookUp(group).getUsers());
    }

    @RequestMapping("addRole")
    @ResponseBody
    public void addRole(@JsonBody("group") GroupDTO group, @JsonBody("role") RoleDTO role) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role);
        Group attachedGroup = groupRepository.refreshWithOptimisticLocking(group);

        attachedGroup.addRole(attachedRole);
    }

    @RequestMapping("addRoles")
    @ResponseBody
    public void addRoles(@JsonBody("group") GroupDTO group, @JsonBody("roles") List<RoleDTO> roles) {
        Group attachedGroup = groupRepository.refreshWithOptimisticLocking(group);
        Collection<Role> attachedRoles = roleRepository.refreshWithOptimisticLocking(roles);

        attachedGroup.addRoles(attachedRoles);
    }

    @RequestMapping("removeRole")
    @ResponseBody
    public void removeRole(@JsonBody("group") GroupDTO group, @JsonBody("role") RoleDTO role) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role);
        Group attachedGroup = groupRepository.refreshWithOptimisticLocking(group);

        attachedGroup.removeRole(attachedRole);
    }

    @RequestMapping("refresh")
    @ResponseBody
    public GroupDTO refresh(@RequestBody GroupDTO group) {
        return GroupDTOAssembler.assemble(groupRepository.lookUp(group));
    }

}
