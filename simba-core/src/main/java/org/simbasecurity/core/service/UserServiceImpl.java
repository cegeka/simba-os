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
import org.simbasecurity.core.audit.ManagementAudit;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.GroupRepository;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.communication.reset.password.ForgotPassword;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordByManager;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordService;
import org.simbasecurity.core.service.filter.EntityFilterService;
import org.simbasecurity.core.service.thrift.ThriftAssembler;
import org.simbasecurity.core.service.user.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.simbasecurity.common.util.StringUtil.join;
import static org.simbasecurity.core.domain.user.EmailAddress.email;

@Transactional
@Service("userService")
public class UserServiceImpl implements UserService, org.simbasecurity.api.service.thrift.UserService.Iface {

    @Autowired private ManagementAudit managementAudit;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PolicyRepository policyRepository;
    @Autowired private GroupRepository groupRepository;

    @Autowired private EntityFilterService filterService;
    @Autowired private UserFactory userFactory;

    @Autowired private ThriftAssembler assembler;
    @Autowired private ResetPasswordService resetPasswordService;
    @Autowired private ResetPasswordByManager resetPasswordByManager;

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

        managementAudit.log("Role ''{0}'' removed from user ''{1}''", attachedRole.getName(), attachedUser.getUserName());

        attachedUser.removeRole(attachedRole);
    }

    public void addRoles(TUser user, Set<TRole> roles) {
        User attachedUser = userRepository.refreshWithOptimisticLocking(user.getId(), user.getVersion());
        Collection<Role> attachedRoles =
                roles.stream()
                     .map(r -> roleRepository.refreshWithOptimisticLocking(r.getId(), r.getVersion()))
                     .collect(Collectors.toList());

        managementAudit.log("Roles ''{0}'' added to user ''{1}''", join(attachedRoles, Role::getName), attachedUser.getUserName());

        attachedUser.addRoles(attachedRoles);
    }

    public List<TPolicy> findPolicies(TUser user) {
        return assembler.list(
                filterService.filterPolicies(policyRepository.find(userRepository.findByName(user.getUserName()))));
    }

    public List<TGroup> findGroups(TUser user) {
        return assembler.list(groupRepository.find(userRepository.lookUp(assembleUser(user))));
    }

    private User assembleUser(TUser user) {
        return assembler.assemble(user);
    }

    public TUser resetPassword(TUser user) {
        User attachedUser = userRepository.findByName(user.getUserName());

        if (attachedUser.getEmail() == null) { throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_REQUIRED);}
        resetPasswordService.sendResetPasswordMessageTo(attachedUser, resetPasswordByManager);

        managementAudit.log("Password for user ''{0}'' resetted", attachedUser.getUserName());

        return assembler.assemble(attachedUser);
    }

    @Override
    public TUser create(TUser user) throws TException {
        return assembler.assemble(userFactory.create(assembleUser(user)));
    }

    @Override
    public TUser createWithRoles(TUser user, List<String> roleNames) throws TException {
        return assembler.assemble(userFactory.createWithRoles(assembleUser(user), roleNames));
    }

    @Override
    public TUser cloneUser(TUser user, String clonedUsername) throws TException {
        return assembler.assemble(userFactory.cloneUser(assembleUser(user), clonedUsername));
    }

    @Override
    public String createRestUser(String username) throws TException {
        return userFactory.createRestUser(username);
    }

    @Override
    public TUser update(TUser user) throws TException {
        User attachedUser = userRepository.refreshWithOptimisticLocking(user.getId(), user.getVersion());

        logUserPropertyChange(user, attachedUser.getFirstName(), user.getFirstName(), "first name");
        attachedUser.setFirstName(user.getFirstName());

        logUserPropertyChange(user, attachedUser.getName(), user.getName(), "name");
        attachedUser.setName(user.getName());

        logUserPropertyChange(user, String.valueOf(attachedUser.getLanguage()), user.getLanguage(), "language");
        attachedUser.setLanguage(Language.valueOf(user.getLanguage()));

        logUserPropertyChange(user, attachedUser.isChangePasswordOnNextLogon(), user.isMustChangePassword(), "password must change");
        attachedUser.setChangePasswordOnNextLogon(user.isMustChangePassword());

        logUserPropertyChange(user, attachedUser.getStatus().name(), user.getStatus(), "status");
        attachedUser.setStatus(Status.valueOf(user.getStatus()));

        logUserPropertyChange(user, attachedUser.getSuccessURL(), user.getSuccessURL(), "success URL");
        attachedUser.setSuccessURL(user.getSuccessURL());

        logUserPropertyChange(user, attachedUser.getEmail(), user.getEmail(), "e-mail");
        attachedUser.setEmail(email(user.getEmail()));

        userRepository.flush();

        return assembler.assemble(attachedUser);
    }

    private void logUserPropertyChange(TUser user, Object oldValue, Object newValue, String property) {
        if (!Objects.equals(oldValue, newValue)) {
            managementAudit.log("User ''{0}'' {3} has changed from ''{1}'' to ''{2}''", user.getUserName(),
                    oldValue, newValue, property);
        }
    }

    @Override
    public TUser refresh(TUser user) throws TException {
        return assembler.assemble(userRepository.lookUp(user.getId()));
    }
}