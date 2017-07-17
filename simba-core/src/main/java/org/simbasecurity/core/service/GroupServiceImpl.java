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
package org.simbasecurity.core.service;

import org.simbasecurity.api.service.thrift.GroupService;
import org.simbasecurity.api.service.thrift.TGroup;
import org.simbasecurity.api.service.thrift.TRole;
import org.simbasecurity.api.service.thrift.TUser;
import org.simbasecurity.core.audit.ManagementAudit;
import org.simbasecurity.core.domain.Group;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.repository.GroupRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.service.thrift.ThriftAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.simbasecurity.common.util.StringUtil.join;

@Transactional
@Service("groupService")
public class GroupServiceImpl implements GroupService.Iface {

    private final GroupRepository groupRepository;
    private final RoleRepository roleRepository;
    private final ThriftAssembler assembler;
    private final ManagementAudit audit;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, RoleRepository roleRepository,
                            ThriftAssembler assembler, ManagementAudit audit) {
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.assembler = assembler;
        this.audit = audit;
    }

    public List<TGroup> findAll() {
        return assembler.list(groupRepository.findAll());
    }

    public List<TRole> findRoles(TGroup group) {
        return assembler.list(groupRepository.lookUp(group.getId()).getRoles());
    }

    public List<TRole> findRolesNotLinked(TGroup group) {
        return assembler.list(roleRepository.findNotLinked(groupRepository.lookUp(group.getId())));
    }

    public List<TUser> findUsers(TGroup group) {
        return assembler.list(groupRepository.lookUp(group.getId()).getUsers());
    }

    public void addRole(TGroup group, TRole role) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role.getId(), role.getVersion());
        Group attachedGroup = groupRepository.refreshWithOptimisticLocking(group.getId(), group.getVersion());

        audit.log("Role ''{0}'' added to group ''{1}''", attachedRole.getName(), attachedGroup.getName());

        attachedGroup.addRole(attachedRole);
    }

    public void addRoles(TGroup group, List<TRole> roles) {
        Group attachedGroup = groupRepository.refreshWithOptimisticLocking(group.getId(), group.getVersion());
        Collection<Role> attachedRoles = roles.stream()
                                              .map(r -> roleRepository.refreshWithOptimisticLocking(r.getId(),
                                                                                                    r.getVersion()))
                                              .collect(Collectors.toList());

        audit.log("Roles ''{0}'' added to group ''{1}''", join(attachedRoles, Role::getName));

        attachedGroup.addRoles(attachedRoles);
    }

    public void removeRole(TGroup group, TRole role) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role.getId(), role.getVersion());
        Group attachedGroup = groupRepository.refreshWithOptimisticLocking(group.getId(), group.getVersion());

        audit.log("Role ''{0}'' removed from group ''{1}''", attachedRole.getName(), attachedGroup.getName());

        attachedGroup.removeRole(attachedRole);
    }

    public TGroup refresh(TGroup group) {
        return assembler.assemble(groupRepository.lookUp(group.getId()));
    }
}
