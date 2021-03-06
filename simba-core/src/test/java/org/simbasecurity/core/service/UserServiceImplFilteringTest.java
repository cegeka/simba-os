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
import org.simbasecurity.core.service.errors.SimbaExceptionHandlingCaller;
import org.simbasecurity.core.service.filter.EntityFilter;
import org.simbasecurity.core.service.filter.EntityFilterService;
import org.simbasecurity.core.service.thrift.ThriftAssembler;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.service.errors.ForwardingThriftHandlerForTests.forwardingThriftHandlerForTests;

public class UserServiceImplFilteringTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private PolicyRepository policyRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRepository userRepository;

    @Spy private EntityFilterService entityFilterService = new EntityFilterService(Optional.empty());
    @Spy private ThriftAssembler assember = new ThriftAssembler(null, null);

    @Spy private SimbaExceptionHandlingCaller simbaExceptionHandlingCaller = new SimbaExceptionHandlingCaller(forwardingThriftHandlerForTests());

    @InjectMocks private UserServiceImpl service;

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
        User userEntity1 = UserTestBuilder.aDefaultUser().withUserName("user-1").build();
        User userEntity2 = UserTestBuilder.aDefaultUser().withUserName("user-2").build();
        User userEntity3 = UserTestBuilder.aDefaultUser().withUserName("user-3").build();

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

        userEntity1.addRole(roleEntity1);
        userEntity2.addRole(roleEntity2);
        userEntity3.addRoles(asList(roleEntity1, roleEntity2));

        roleEntity1.addPolicy(policyEntity1);
        roleEntity2.addPolicy(policyEntity2);

        when(userRepository.findAllOrderedByName()).thenReturn(asList(userEntity1, userEntity2, userEntity3));

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
        assertThat(service.findAll()).extracting(TUser::getUserName).containsExactlyInAnyOrder("user-1");
    }

    @Test
    public void find() throws Exception  {
        assertThat(service.findByRole(tRole01)).extracting(TUser::getUserName).containsExactlyInAnyOrder("user-1");
        assertThat(service.findByRole(tRole02)).extracting(TUser::getUserName).isEmpty();
    }

    @Test
    public void findRoles() throws Exception {
        assertThat(service.findRoles(tUser01)).extracting(TRole::getName).containsExactlyInAnyOrder("role-1");
        assertThat(service.findRoles(tUser02)).extracting(TRole::getName).isEmpty();
        assertThat(service.findRoles(tUser03)).extracting(TRole::getName).containsExactlyInAnyOrder("role-1");
    }

    @Test
    public void findRolesNotLinked() throws Exception {
        assertThat(service.findRolesNotLinked(tUser01)).extracting(TRole::getName).isEmpty();
        assertThat(service.findRolesNotLinked(tUser02)).extracting(TRole::getName).containsExactlyInAnyOrder("role-1");
        assertThat(service.findRolesNotLinked(tUser03)).extracting(TRole::getName).isEmpty();
    }

    @Test
    public void findPolicies() throws Exception {
        assertThat(service.findPolicies(tUser01)).extracting(TPolicy::getName).containsExactlyInAnyOrder("policy-1");
        assertThat(service.findPolicies(tUser02)).extracting(TPolicy::getName).isEmpty();
        assertThat(service.findPolicies(tUser03)).extracting(TPolicy::getName).containsExactlyInAnyOrder("policy-1");

    }
}
