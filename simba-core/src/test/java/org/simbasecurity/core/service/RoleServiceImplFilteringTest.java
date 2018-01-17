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
import org.simbasecurity.core.domain.*;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.core.service.filter.EntityFilter;
import org.simbasecurity.core.service.filter.EntityFilterService;
import org.simbasecurity.core.service.thrift.ThriftAssembler;
import org.simbasecurity.test.LocatorTestCase;
import org.simbasecurity.test.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class RoleServiceImplFilteringTest extends LocatorTestCase {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private PolicyRepository policyRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRepository userRepository;

    @Spy private EntityFilterService entityFilterService = new EntityFilterService(Optional.empty());
    @Spy private ThriftAssembler assembler = new ThriftAssembler();

    @InjectMocks private RoleServiceImpl roleService;

    @Mock private TRole tRole01;
    @Mock private TRole tRole02;
    @Mock private TRole tRole03;

    private PolicyEntity policyEntity1 = new PolicyEntity("policy-1");
    private PolicyEntity policyEntity2 = new PolicyEntity("policy-2");
    private RoleEntity roleEntity1 = new RoleEntity("role-1");
    private RoleEntity roleEntity2 = new RoleEntity("role-2");
    private RoleEntity roleEntity3 = new RoleEntity("role-3");

    private List<EntityFilter> filterServices = new ArrayList<>();

    @Before
    public void setup() {
        implantMock(UserValidator.class);
        implantMock(PasswordValidator.class);
        implantMock(CoreConfigurationService.class);

        User userEntity1 = UserTestBuilder.aDefaultUser().withUserName("user-1").build();
        User userEntity2 = UserTestBuilder.aDefaultUser().withUserName("user-2").build();

        filterServices.add(new EntityFilter() {
            @Override
            public Collection<Role> filterRoles(Collection<Role> roles) {
                return roles.stream().filter(r -> r.getName().endsWith("-1")).collect(Collectors.toList());
            }

            @Override
            public Collection<Policy> filterPolicies(Collection<Policy> policies) {
                return policies.stream().filter(p -> p.getName().endsWith("-1")).collect(Collectors.toList());
            }

            @Override
            public Collection<User> filterUsers(Collection<User> users) {
                return users.stream().filter(u -> u.getUserName().endsWith("-1")).collect(Collectors.toList());
            }
        });

        ReflectionUtil.setField(entityFilterService, "filters", filterServices);

        when(tRole01.getId()).thenReturn(1L);
        when(tRole02.getId()).thenReturn(2L);
        when(tRole03.getId()).thenReturn(3L);


        roleEntity1.addPolicy(policyEntity1);
        roleEntity2.addPolicy(policyEntity2);
        roleEntity3.addPolicy(policyEntity1);
        roleEntity3.addPolicy(policyEntity2);

        roleEntity1.addUser(userEntity1);
        roleEntity2.addUser(userEntity2);
        roleEntity3.addUser(userEntity1);
        roleEntity3.addUser(userEntity2);

        Collection<Role> roles = asList(roleEntity1, roleEntity2, roleEntity3);
        when(roleRepository.findAll()).thenReturn(roles);

        when(roleRepository.lookUp(1)).thenReturn(roleEntity1);
        when(roleRepository.lookUp(2)).thenReturn(roleEntity2);
        when(roleRepository.lookUp(3)).thenReturn(roleEntity3);

        when(policyRepository.findNotLinked(roleEntity1)).thenReturn(singletonList(policyEntity2));
        when(policyRepository.findNotLinked(roleEntity2)).thenReturn(singletonList(policyEntity1));
        when(policyRepository.findNotLinked(roleEntity3)).thenReturn(emptyList());

        when(policyRepository.findForRole(roleEntity1)).thenReturn(singletonList(policyEntity1));
        when(policyRepository.findForRole(roleEntity2)).thenReturn(singletonList(policyEntity2));
        when(policyRepository.findForRole(roleEntity3)).thenReturn(asList(policyEntity1, policyEntity2));

        when(userRepository.findNotLinked(roleEntity1)).thenReturn(singletonList(userEntity2));
        when(userRepository.findNotLinked(roleEntity2)).thenReturn(singletonList(userEntity1));
        when(userRepository.findNotLinked(roleEntity3)).thenReturn(emptyList());

        when(userRepository.findForRole(roleEntity1)).thenReturn(singletonList(userEntity1));
        when(userRepository.findForRole(roleEntity2)).thenReturn(singletonList(userEntity2));
        when(userRepository.findForRole(roleEntity3)).thenReturn(asList(userEntity1, userEntity2));
    }

    @Test
    public void findAll() throws Exception {
        assertThat(roleService.findAll()).extracting(TRole::getName).containsExactlyInAnyOrder("role-1");
    }

    @Test
    public void findPolicies() throws Exception {
        assertThat(roleService.findPolicies(tRole01)).extracting(TPolicy::getName).containsExactly("policy-1");
        assertThat(roleService.findPolicies(tRole02)).extracting(TPolicy::getName).isEmpty();
        assertThat(roleService.findPolicies(tRole03)).extracting(TPolicy::getName).containsExactly("policy-1");
    }

    @Test
    public void findPoliciesNotLinked() throws Exception {
        assertThat(roleService.findPoliciesNotLinked(tRole01)).extracting(TPolicy::getName).isEmpty();
        assertThat(roleService.findPoliciesNotLinked(tRole02)).extracting(TPolicy::getName).containsExactly("policy-1");
        assertThat(roleService.findPoliciesNotLinked(tRole03)).extracting(TPolicy::getName).isEmpty();
    }

    @Test
    public void findUsers() throws Exception {
        assertThat(roleService.findUsers(tRole01)).extracting(TUser::getUserName).containsExactly("user-1");
        assertThat(roleService.findUsers(tRole02)).extracting(TUser::getUserName).isEmpty();
        assertThat(roleService.findUsers(tRole03)).extracting(TUser::getUserName).containsExactly("user-1");
    }

    @Test
    public void findUsersNotLinked() throws Exception {
        assertThat(roleService.findUsersNotLinked(tRole01)).extracting(TUser::getUserName).isEmpty();
        assertThat(roleService.findUsersNotLinked(tRole02)).extracting(TUser::getUserName).containsExactly("user-1");
        assertThat(roleService.findUsersNotLinked(tRole03)).extracting(TUser::getUserName).isEmpty();
    }
}