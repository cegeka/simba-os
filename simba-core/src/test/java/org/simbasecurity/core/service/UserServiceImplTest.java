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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.api.service.thrift.TPolicy;
import org.simbasecurity.api.service.thrift.TRole;
import org.simbasecurity.api.service.thrift.TUser;
import org.simbasecurity.core.audit.ManagementAudit;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.*;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordByManager;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordService;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.core.service.errors.SimbaExceptionHandlingCaller;
import org.simbasecurity.core.service.filter.EntityFilter;
import org.simbasecurity.core.service.filter.EntityFilterService;
import org.simbasecurity.core.service.thrift.ThriftAssembler;
import org.simbasecurity.test.LocatorRule;
import org.simbasecurity.test.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.service.errors.ForwardingThriftHandlerForTests.forwardingThriftHandlerForTests;

public class UserServiceImplTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    @Rule public LocatorRule locatorRule = LocatorRule.locator();

    @Mock private ResetPasswordService resetPasswordService;
    @Mock private ResetPasswordByManager resetReason;
    @Mock private ManagementAudit managementAudit;

    @Mock private PolicyRepository policyRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRepository userRepository;

    @Spy private EntityFilterService entityFilterService = new EntityFilterService(Optional.empty());
    @Spy private ThriftAssembler assembler = new ThriftAssembler(null);
    @Spy private SimbaExceptionHandlingCaller simbaExceptionHandlingCaller = new SimbaExceptionHandlingCaller(forwardingThriftHandlerForTests());
    @InjectMocks private UserServiceImpl service;

    private CoreConfigurationService configurationService;

    private TRole tRole01 = new TRole(1, 1, "role-1");
    private TRole tRole02 = new TRole(2, 1, "role-2");

    private TUser tUser01 = new TUser().setId(1).setVersion(1).setUserName("user-1");
    private TUser tUser02 = new TUser().setId(2).setVersion(1).setUserName("user-2");
    private TUser tUser03 = new TUser().setId(3).setVersion(1).setUserName("user-3");

    private PolicyEntity policyEntity1 = new PolicyEntity("policy-1");
    private PolicyEntity policyEntity2 = new PolicyEntity("policy-2");
    private RoleEntity roleEntity1 = new RoleEntity("role-1");
    private RoleEntity roleEntity2 = new RoleEntity("role-2");

    private List<EntityFilter> filterServices = new ArrayList<>();

    @Before
    public void setup() {
        locatorRule.implantMock(UserValidator.class);
        locatorRule.implantMock(PasswordValidator.class);
        configurationService = locatorRule.getCoreConfigurationService();
        when(configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(true);
        service.setConfigurationService(configurationService);

        User userEntity1 = aDefaultUser().withUserName("user-1").build();
        User userEntity2 = aDefaultUser().withUserName("user-2").build();
        User userEntity3 = aDefaultUser().withUserName("user-3").build();

        ReflectionUtil.setField(entityFilterService, "filters", filterServices);

        userEntity1.addRole(roleEntity1);
        userEntity2.addRole(roleEntity2);
        userEntity3.addRoles(asList(roleEntity1, roleEntity2));

        roleEntity1.addPolicy(policyEntity1);
        roleEntity2.addPolicy(policyEntity2);

        when(userRepository.findAll()).thenReturn(asList(userEntity1, userEntity2, userEntity3));
        when(userRepository.findAllOrderedByName()).thenReturn(asList(userEntity1, userEntity2, userEntity3));
        when(userRepository.searchUsersOrderedByName("1")).thenReturn(asList(userEntity1));

        when(userRepository.findByName("user-1")).thenReturn(userEntity1);
        when(userRepository.findByName("user-2")).thenReturn(userEntity2);
        when(userRepository.findByName("user-3")).thenReturn(userEntity3);

        when(userRepository.findForRole(roleEntity1)).thenReturn(asList(userEntity1, userEntity3));
        when(userRepository.findForRole(roleEntity2)).thenReturn(asList(userEntity2, userEntity3));

        when(roleRepository.lookUp(any(RoleEntity.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            switch (role.getName()) {
                case "role-1":
                    return roleEntity1;
                case "role-2":
                    return roleEntity2;
            }
            return null;
        });

        when(roleRepository.findForUser(userEntity1)).thenReturn(singletonList(roleEntity1));
        when(roleRepository.findForUser(userEntity2)).thenReturn(singletonList(roleEntity2));
        when(roleRepository.findForUser(userEntity3)).thenReturn(asList(roleEntity1, roleEntity2));

        when(roleRepository.findNotLinked(userEntity1)).thenReturn(singletonList(roleEntity2));
        when(roleRepository.findNotLinked(userEntity2)).thenReturn(singletonList(roleEntity1));
        when(roleRepository.findNotLinked(userEntity3)).thenReturn(emptyList());

        when(policyRepository.find(userEntity1)).thenReturn(singletonList(policyEntity1));
        when(policyRepository.find(userEntity2)).thenReturn(singletonList(policyEntity2));
        when(policyRepository.find(userEntity3)).thenReturn(asList(policyEntity1, policyEntity2));
    }

    @Test
    public void findAll() throws Exception {
        assertThat(service.findAll()).extracting(TUser::getUserName).containsExactlyInAnyOrder("user-1", "user-2", "user-3");
    }

    @Test
    public void find() throws Exception {
        assertThat(service.findByRole(tRole01)).extracting(TUser::getUserName).containsExactlyInAnyOrder("user-1", "user-3");
        assertThat(service.findByRole(tRole02)).extracting(TUser::getUserName).containsExactlyInAnyOrder("user-2", "user-3");
    }

    @Test
    public void findRoles() throws Exception {
        assertThat(service.findRoles(tUser01)).extracting(TRole::getName).containsExactlyInAnyOrder("role-1");
        assertThat(service.findRoles(tUser02)).extracting(TRole::getName).containsExactlyInAnyOrder("role-2");
        assertThat(service.findRoles(tUser03)).extracting(TRole::getName).containsExactlyInAnyOrder("role-1", "role-2");
    }

    @Test
    public void findRolesNotLinked() throws Exception {
        assertThat(service.findRolesNotLinked(tUser01)).extracting(TRole::getName).containsExactlyInAnyOrder("role-2");
        assertThat(service.findRolesNotLinked(tUser02)).extracting(TRole::getName).containsExactlyInAnyOrder("role-1");
        assertThat(service.findRolesNotLinked(tUser03)).extracting(TRole::getName).isEmpty();
    }

    @Test
    public void findPolicies() throws Exception {
        assertThat(service.findPolicies(tUser01)).extracting(TPolicy::getName).containsExactlyInAnyOrder("policy-1");
        assertThat(service.findPolicies(tUser02)).extracting(TPolicy::getName).containsExactlyInAnyOrder("policy-2");
        assertThat(service.findPolicies(tUser03)).extracting(TPolicy::getName).containsExactlyInAnyOrder("policy-1", "policy-2");

    }

    @Test
    public void search() throws Exception {
        assertThat(service.search("1")).extracting(TUser::getUserName).containsExactly("user-1");
        verify(userRepository).searchUsersOrderedByName("1");
    }

    @Test
    public void resetPassword() throws Exception {
        User user = aDefaultUser().withUserName("user-1").build();
        when(userRepository.findByName(tUser01.getUserName())).thenReturn(user);
        TUser expectedUser = new TUser();
        when(assembler.assemble(user)).thenReturn(expectedUser);

        TUser tUser = service.resetPassword(tUser01);

        assertThat(tUser).isEqualTo(expectedUser);
        verify(resetPasswordService).sendResetPasswordMessageTo(user, resetReason);
        verify(managementAudit).log("Password for user ''{0}'' has been reset", "user-1");
    }

    @Test
    public void update_LogsOnlyWhenDifferent() throws Exception {
        User userFromDB = aDefaultUser()
                .withUserName("user-1")
                .withEmail("bruce@richorphan.com")
                .build();

        when(userRepository.refreshWithOptimisticLocking(tUser01.getId(),tUser01.getVersion())).thenReturn(userFromDB);
        tUser01.setEmail("bruce@wayneindustries.com");
        tUser01.setName("Wayne");
        tUser01.setFirstName("bruce");
        tUser01.setStatus("ACTIVE");
        tUser01.setLanguage("en_US");
        tUser01.setMustChangePassword(true);

        service.update(tUser01);

        verify(managementAudit).log("User ''{0}'' {3} has changed from ''{1}'' to ''{2}''", "user-1",
                "bruce@richorphan.com", "bruce@wayneindustries.com", "e-mail");
    }

    @Test
    public void update_DoesNotLogWhenSameEmail() throws Exception {
        User userFromDB = aDefaultUser()
                .withUserName("user-1")
                .withEmail("bruce@wayneindustries.com")
                .build();

        when(userRepository.refreshWithOptimisticLocking(tUser01.getId(),tUser01.getVersion())).thenReturn(userFromDB);
        tUser01.setEmail(UserTestBuilder.EMAIL);
        tUser01.setName(UserTestBuilder.NAME);
        tUser01.setFirstName(UserTestBuilder.FIRST_NAME);
        tUser01.setStatus("ACTIVE");
        tUser01.setLanguage("en_US");
        tUser01.setMustChangePassword(true);

        service.update(tUser01);

        verifyZeroInteractions(managementAudit);
    }

    @Test
    public void update_DoesNotLogWhenEmailRemainsNull_AndDoesNotThrowException() throws Exception {
        User userFromDB = aDefaultUser()
                .withUserName("user-1")
                .withoutEmail()
                .build();

        when(userRepository.refreshWithOptimisticLocking(tUser01.getId(),tUser01.getVersion())).thenReturn(userFromDB);
        tUser01.setEmail(null);
        tUser01.setName(UserTestBuilder.NAME);
        tUser01.setFirstName(UserTestBuilder.FIRST_NAME);
        tUser01.setStatus("ACTIVE");
        tUser01.setLanguage("en_US");
        tUser01.setMustChangePassword(true);

        when(configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);

        service.update(tUser01);

        verifyZeroInteractions(managementAudit);
    }

    @Test
    public void update_WhenEmailWasBlankedOut_EmailNotRequiredAccordingToParameter_ThenNoMailIsSent_AndGetsLogged() throws Exception {
        User userFromDB = aDefaultUser()
                .withUserName("user-1")
                .withEmail("bruce@wayneindustries.com")
                .build();

        when(userRepository.refreshWithOptimisticLocking(tUser01.getId(),tUser01.getVersion())).thenReturn(userFromDB);
        tUser01.setEmail("");
        tUser01.setName(UserTestBuilder.NAME);
        tUser01.setFirstName(UserTestBuilder.FIRST_NAME);
        tUser01.setStatus("ACTIVE");
        tUser01.setLanguage("en_US");
        tUser01.setMustChangePassword(true);

        when(configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);

        TUser actual = service.update(tUser01);

        verify(managementAudit).log("User ''{0}'' {3} has changed from ''{1}'' to ''{2}''", userFromDB.getUserName(), "bruce@wayneindustries.com", "", "e-mail");
        verifyZeroInteractions(resetPasswordService);

        assertThat(actual.getEmail()).isEqualTo(EmailAddress.emptyEmail().asString());
    }

    @Test
    public void update_WhenEmailChanged_EmailNotRequiredAccordingToParameter_sendResetPasswordEmail() throws Exception {
        User userFromDB = aDefaultUser()
                .withUserName("user-1")
                .withEmail("bruce@richorphan.com").build();

        when(userRepository.refreshWithOptimisticLocking(tUser01.getId(),tUser01.getVersion())).thenReturn(userFromDB);
        tUser01.setEmail("bruce@wayneindustries.com");
        tUser01.setName("Wayne");
        tUser01.setFirstName("bruce");
        tUser01.setStatus("ACTIVE");
        tUser01.setLanguage("en_US");
        tUser01.setMustChangePassword(true);

        when(configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);

        TUser actual = service.update(tUser01);

        verify(managementAudit).log("User ''{0}'' {3} has changed from ''{1}'' to ''{2}''", userFromDB.getUserName(),
                "bruce@richorphan.com", "bruce@wayneindustries.com", "e-mail");
        verify(resetPasswordService).sendResetPasswordMessageTo(userFromDB, resetReason);

        assertThat(actual.getEmail()).isEqualTo("bruce@wayneindustries.com");
    }

    @Test
    public void update_WhenEmailChanged_sendResetPasswordEmail() throws Exception {
        User userFromDB = aDefaultUser()
                .withUserName("user-1")
                .withEmail("bruce@richorphan.com").build();

        when(userRepository.refreshWithOptimisticLocking(tUser01.getId(),tUser01.getVersion())).thenReturn(userFromDB);
        tUser01.setEmail("bruce@wayneindustries.com");
        tUser01.setName("Wayne");
        tUser01.setFirstName("bruce");
        tUser01.setStatus("ACTIVE");
        tUser01.setLanguage("en_US");
        tUser01.setMustChangePassword(true);

        TUser actual = service.update(tUser01);

        verify(managementAudit).log("Password for user ''{0}'' has been reset", userFromDB.getUserName());

        assertThat(actual.getEmail()).isEqualTo(EmailAddress.email("bruce@wayneindustries.com").asString());
    }

    @Test
    public void update_WhenEmailRemainsNull_EmailRequiredAccordingToParameter_ThenSimbaExceptionIsThrown() throws Exception {
        User userFromDB = aDefaultUser()
                .withUserName("user-1")
                .withoutEmail()
                .build();

        when(userRepository.refreshWithOptimisticLocking(tUser01.getId(),tUser01.getVersion())).thenReturn(userFromDB);
        tUser01.setEmail(null);
        tUser01.setName(UserTestBuilder.NAME);
        tUser01.setFirstName(UserTestBuilder.FIRST_NAME);
        tUser01.setStatus("ACTIVE");
        tUser01.setLanguage("en_US");
        tUser01.setMustChangePassword(true);

        assertThatThrownBy(() -> service.update(tUser01))
            .isInstanceOf(SimbaException.class)
            .hasMessage("EMAIL_ADDRESS_REQUIRED");
    }

    @Test
    public void update_WhenEmailWasBlankedOut_EmailRequiredAccordingToParameter_ThenSimbaExceptionIsThrown() throws Exception {
        User userFromDB = aDefaultUser()
                .withUserName("user-1")
                .withEmail("bruce@wayneindustries.com")
                .build();

        when(userRepository.refreshWithOptimisticLocking(tUser01.getId(),tUser01.getVersion())).thenReturn(userFromDB);
        tUser01.setEmail(null);
        tUser01.setName(UserTestBuilder.NAME);
        tUser01.setFirstName(UserTestBuilder.FIRST_NAME);
        tUser01.setStatus("ACTIVE");
        tUser01.setLanguage("en_US");
        tUser01.setMustChangePassword(true);

        assertThatThrownBy(() -> service.update(tUser01))
            .isInstanceOf(SimbaException.class)
            .hasMessage("EMAIL_ADDRESS_REQUIRED");
    }
}
