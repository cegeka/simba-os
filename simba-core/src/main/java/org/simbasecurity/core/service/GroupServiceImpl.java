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

import org.apache.thrift.TException;
import org.simbasecurity.api.service.thrift.GroupService;
import org.simbasecurity.api.service.thrift.TGroup;
import org.simbasecurity.api.service.thrift.TRole;
import org.simbasecurity.api.service.thrift.TUser;
import org.simbasecurity.core.audit.ManagementAudit;
import org.simbasecurity.core.domain.Group;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.repository.GroupRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.service.errors.SimbaExceptionHandlingCaller;
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
    private final SimbaExceptionHandlingCaller simbaExceptionHandlingCaller;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, RoleRepository roleRepository,
                            ThriftAssembler assembler, ManagementAudit audit, SimbaExceptionHandlingCaller simbaExceptionHandlingCaller) {
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.assembler = assembler;
        this.audit = audit;
        this.simbaExceptionHandlingCaller = simbaExceptionHandlingCaller;
    }

    @Override
    public List<TGroup> findAll() throws TException {
        return simbaExceptionHandlingCaller.call(() -> {
            return assembler.list(groupRepository.findAll());
        });
    }

    @Override
    public List<TRole> findRoles(TGroup group) throws TException {
        return simbaExceptionHandlingCaller.call(() -> {
            return assembler.list(groupRepository.lookUp(group.getId()).getRoles());
        });
    }

    @Override
    public List<TRole> findRolesNotLinked(TGroup group) throws TException {
        return simbaExceptionHandlingCaller.call(() -> {
            return assembler.list(roleRepository.findNotLinked(groupRepository.lookUp(group.getId())));
        });
    }

    @Override
    public List<TUser> findUsers(TGroup group) throws TException {
        return simbaExceptionHandlingCaller.call(() -> {
            return assembler.list(groupRepository.lookUp(group.getId()).getUsers());
        });
    }

    @Override
    public void addRole(TGroup group, TRole role) throws TException {
        simbaExceptionHandlingCaller.call(() -> {
            Role attachedRole = roleRepository.refreshWithOptimisticLocking(role.getId(), role.getVersion());
            Group attachedGroup = groupRepository.refreshWithOptimisticLocking(group.getId(), group.getVersion());

            audit.log("Role ''{0}'' added to group ''{1}''", attachedRole.getName(), attachedGroup.getName());

            attachedGroup.addRole(attachedRole);
        });
    }

    @Override
    public void addRoles(TGroup group, List<TRole> roles) throws TException {
        simbaExceptionHandlingCaller.call(() -> {
            Group attachedGroup = groupRepository.refreshWithOptimisticLocking(group.getId(), group.getVersion());
            Collection<Role> attachedRoles = roles.stream()
                    .map(r -> roleRepository.refreshWithOptimisticLocking(r.getId(),
                            r.getVersion()))
                    .collect(Collectors.toList());

            audit.log("Roles ''{0}'' added to group ''{1}''", join(attachedRoles, Role::getName));

            attachedGroup.addRoles(attachedRoles);
        });
    }

    @Override
    public void removeRole(TGroup group, TRole role) throws TException {
        simbaExceptionHandlingCaller.call(() -> {
            Role attachedRole = roleRepository.refreshWithOptimisticLocking(role.getId(), role.getVersion());
            Group attachedGroup = groupRepository.refreshWithOptimisticLocking(group.getId(), group.getVersion());

            audit.log("Role ''{0}'' removed from group ''{1}''", attachedRole.getName(), attachedGroup.getName());

            attachedGroup.removeRole(attachedRole);
        });
    }

    @Override
    public TGroup refresh(TGroup group) throws TException {
        return simbaExceptionHandlingCaller.call(() -> {
            return assembler.assemble(groupRepository.lookUp(group.getId()));
        });
    }
}
