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

import org.owasp.esapi.errors.ValidationException;
import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.service.manager.assembler.PolicyDTOAssembler;
import org.simbasecurity.core.service.manager.assembler.RoleAssembler;
import org.simbasecurity.core.service.manager.assembler.RoleDTOAssembler;
import org.simbasecurity.core.service.manager.assembler.UserDTOAssembler;
import org.simbasecurity.core.service.manager.dto.PolicyDTO;
import org.simbasecurity.core.service.manager.dto.RoleDTO;
import org.simbasecurity.core.service.manager.dto.UserDTO;
import org.simbasecurity.core.service.manager.web.JsonBody;
import org.simbasecurity.core.service.validation.DTOValidator;
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
@RequestMapping("role")
public class RoleManagerService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private EntityFilterService filterService;

    @RequestMapping("findAll")
    @ResponseBody
    public Collection<RoleDTO> findAll() {
        return RoleDTOAssembler.assemble(filterService.filterRoles(roleRepository.findAll()));
    }

    @RequestMapping("findPolicies")
    @ResponseBody
    public Collection<PolicyDTO> findPolicies(@RequestBody RoleDTO role) {
        return PolicyDTOAssembler.assemble(filterService.filterPolicies(roleRepository.lookUp(role).getPolicies()));
    }

    @RequestMapping("findPoliciesNotLinked")
    @ResponseBody
    public Collection<PolicyDTO> findPoliciesNotLinked(@RequestBody RoleDTO role) {
        return PolicyDTOAssembler.assemble(filterService.filterPolicies(policyRepository.findNotLinked(roleRepository.lookUp(role))));
    }

    @RequestMapping("findUsers")
    @ResponseBody
    public Collection<UserDTO> findUsers(@RequestBody RoleDTO role) {
        return UserDTOAssembler.assemble(filterService.filterUsers(roleRepository.lookUp(role).getUsers()));
    }

    @RequestMapping("findUsersNotLinked")
    @ResponseBody
    public Collection<UserDTO> findUsersNotLinked(@RequestBody RoleDTO role) {
        return UserDTOAssembler.assemble(filterService.filterUsers(userRepository.findNotLinked(roleRepository.lookUp(role))));
    }

    @RequestMapping("addPolicy")
    @ResponseBody
    public void addPolicy(@JsonBody("role") RoleDTO role, @JsonBody("policy") PolicyDTO policy) {
        final Role attachedRole = roleRepository.refreshWithOptimisticLocking(role);
        final Policy attachedPolicy = policyRepository.refreshWithOptimisticLocking(policy);

        attachedRole.addPolicy(attachedPolicy);
        
        roleRepository.persist(attachedRole);
    }

    @RequestMapping("addPolicies")
    @ResponseBody
    public void addPolicies(@JsonBody("role") RoleDTO role, @JsonBody("policies") List<PolicyDTO> policies) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role);
        Collection<Policy> attachedPolicies = policyRepository.refreshWithOptimisticLocking(policies);

        attachedRole.addPolicies(attachedPolicies);
        
        roleRepository.persist(attachedRole);
    }

    @RequestMapping("removePolicy")
    @ResponseBody
    public void removePolicy(@JsonBody("role") RoleDTO role, @JsonBody("policy") PolicyDTO policy) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role);
        Policy attachedPolicy = policyRepository.refreshWithOptimisticLocking(policy);

        attachedRole.removePolicy(attachedPolicy);
        
        roleRepository.persist(attachedRole);
    }

    @RequestMapping("removeUser")
    @ResponseBody
    public void removeUser(@JsonBody("user") UserDTO user, @JsonBody("role") RoleDTO role) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role);
        User attachedUser = userRepository.refreshWithOptimisticLocking(user);
        attachedRole.removeUser(attachedUser);
        
        roleRepository.persist(attachedRole);
    }

    @RequestMapping("addUsers")
    @ResponseBody
    public void addUsers(@JsonBody("role") RoleDTO role, @JsonBody("users") List<UserDTO> users) {
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role);
        Collection<User> attachedUsers = userRepository.refreshWithOptimisticLocking(users);

        attachedRole.addUsers(attachedUsers);
        
        roleRepository.persist(attachedRole);
    }

    @RequestMapping("refresh")
    @ResponseBody
    public RoleDTO refresh(@RequestBody RoleDTO role) {
        return RoleDTOAssembler.assemble(roleRepository.lookUp(role));
    }

    @RequestMapping("createRole")
    @ResponseBody
    public RoleDTO createRole(@JsonBody("roleName") String roleName) throws ValidationException {
        DTOValidator.assertValidString("createRole", roleName);
        if(roleRepository.findByName(roleName) != null) {
            throw new IllegalArgumentException("Role with name "+roleName+" already exists");
        }
        Role newRole = RoleAssembler.createRole(roleName);
        roleRepository.persist(newRole);
        return RoleDTOAssembler.assemble(newRole);
    }

    @RequestMapping("deleteRole")
    public void deleteRole(@JsonBody("role") RoleDTO role) throws ValidationException {
        DTOValidator.assertValid(role);
        Role roleToDelete = roleRepository.lookUp(role);
        roleRepository.remove(roleToDelete);
    }
}
