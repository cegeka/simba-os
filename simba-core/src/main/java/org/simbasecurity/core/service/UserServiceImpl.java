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
import org.simbasecurity.api.service.thrift.TGroup;
import org.simbasecurity.api.service.thrift.TPolicy;
import org.simbasecurity.api.service.thrift.TRole;
import org.simbasecurity.api.service.thrift.TUser;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.generator.PasswordGenerator;
import org.simbasecurity.core.domain.repository.GroupRepository;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;
import org.simbasecurity.core.service.filter.EntityFilterService;
import org.simbasecurity.core.service.thrift.ThriftAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.PASSWORD_CHANGE_REQUIRED;
import static org.simbasecurity.core.exception.SimbaMessageKey.USER_ALREADY_EXISTS;

@Transactional
@Service("userService")
public class UserServiceImpl implements UserService, org.simbasecurity.api.service.thrift.UserService.Iface {

    @Autowired private Audit audit;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PolicyRepository policyRepository;
    @Autowired private GroupRepository groupRepository;

    @Autowired private AuditLogEventFactory eventFactory;

    @Autowired private EntityFilterService filterService;
    @Autowired private ConfigurationServiceImpl configurationService;

    @Autowired private ThriftAssembler assembler;
    @Autowired private PasswordGenerator passwordGenerator;

    @Override
    public User create(User user, List<String> roleNames) {
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new IllegalArgumentException("Role name " + roleName + " doesn't exist");
            }
            user.addRole(role);
        }

        if (userRepository.findByName(user.getUserName()) != null) {
            throw new SimbaException(USER_ALREADY_EXISTS, user.getUserName());
        }

        User newUser = userRepository.persist(user);

        audit.log(eventFactory.createEventForSession(user.getUserName(), null, "", "User created"));
        return newUser;
    }

    @Override
    public User findByName(String userName) {
        return userRepository.findByName(userName);
    }

    public List<TUser> findAll() {
        return assembler.list(filterService.filterUsers(userRepository.findAllOrderedByName()));
    }

    public List<TUser> search(String searchText) {
        return assembler.list(filterService.filterUsers(userRepository.searchUsersOrderedByName(searchText)));
    }

    public List<TUser> findByRole(TRole role) {
        return assembler.list(
                filterService.filterUsers(userRepository.findForRole(roleRepository.lookUp(assembler.assemble(role)))));
    }

    public List<TRole> findRoles(TUser user) {
        return assembler.list(
                filterService.filterRoles(roleRepository.findForUser(userRepository.findByName(user.getUserName()))));
    }

    public List<TRole> findRolesNotLinked(TUser user) {
        return assembler.list(filterService.filterRoles(
                roleRepository.findNotLinked(userRepository.findByName(user.getUserName()))));
    }

    public void removeRole(TUser user, TRole role) {
        User attachedUser = userRepository.refreshWithOptimisticLocking(user.getId(), user.getVersion());
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role.getId(), role.getVersion());

        attachedUser.removeRole(attachedRole);
    }

    public void addRoles(TUser user, Set<TRole> roles) {
        User attachedUser = userRepository.refreshWithOptimisticLocking(user.getId(), user.getVersion());
        Collection<Role> attachedRoles =
                roles.stream()
                     .map(r -> roleRepository.refreshWithOptimisticLocking(r.getId(), r.getVersion()))
                     .collect(Collectors.toList());

        attachedUser.addRoles(attachedRoles);
    }

    public List<TPolicy> findPolicies(TUser user) {
        return assembler.list(
                filterService.filterPolicies(policyRepository.find(userRepository.findByName(user.getUserName()))));
    }

    public List<TGroup> findGroups(TUser user) {
        return assembler.list(groupRepository.find(userRepository.lookUp(assembler.assemble(user))));
    }

    public TUser resetPassword(TUser user) {
        User attachedUser = userRepository.refreshWithOptimisticLocking(user.getUserName(), user.getVersion());
        attachedUser.resetPassword();
        userRepository.flush();
        return assembler.assemble(attachedUser);
    }

    @Override
    public TUser create(TUser user) throws TException {
        return assembler.assemble(createUser(user));
    }

    @Override
    public TUser createWithRoles(TUser user, List<String> roleNames) throws TException {
        User newUser = createUser(user);
        roleNames.stream()
                 .map(n -> roleRepository.findByName(n))
                 .filter(Objects::nonNull)
                 .forEach(newUser::addRole);
        return assembler.assemble(newUser);
    }

    @Override
    public TUser cloneUser(TUser user, String clonedUsername) throws TException {
        Set<Role> roles = userRepository.findByName(clonedUsername).getRoles();
        User newUser = createUser(user);
        newUser.addRoles(roles);
        return assembler.assemble(newUser);
    }

    @Override
    public String createRestUser(String username) throws TException {
        User newUser = createUser(new TUser().setUserName(username)
                                             .setPasswordChangeRequired(false)
                                             .setMustChangePassword(false)
                                             .setLanguage(Language.nl_NL.name())
                                             .setStatus(Status.ACTIVE.name()));
        User attachedUser = userRepository.persist(newUser);
        String password = passwordGenerator.generatePassword();
        attachedUser.changePassword(password, password);
        return password;
    }

    private User createUser(TUser user) {
        if (userRepository.findByName(user.getUserName()) != null) {
            throw new SimbaException(USER_ALREADY_EXISTS, user.getUserName());
        }

        Boolean passwordChangeRequired = configurationService.getValue(PASSWORD_CHANGE_REQUIRED);
        user.setPasswordChangeRequired(passwordChangeRequired);

        return userRepository.persist(assembler.assemble(user));
    }

    @Override
    public TUser update(TUser user) throws TException {
        User attachedUser = userRepository.refreshWithOptimisticLocking(user.getId(), user.getVersion());
        attachedUser.setFirstName(user.getFirstName());
        attachedUser.setName(user.getName());
        attachedUser.setLanguage(Language.valueOf(user.getLanguage()));
        attachedUser.setChangePasswordOnNextLogon(user.isMustChangePassword());
        attachedUser.setStatus(Status.valueOf(user.getStatus()));
        attachedUser.setSuccessURL(user.getSuccessURL());
        userRepository.flush();
        return assembler.assemble(attachedUser);
    }

    @Override
    public TUser refresh(TUser user) throws TException {
        return assembler.assemble(userRepository.lookUp(user.getId()));
    }
}