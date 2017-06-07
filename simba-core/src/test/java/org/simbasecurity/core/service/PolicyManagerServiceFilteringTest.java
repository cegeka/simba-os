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
import org.simbasecurity.core.domain.*;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
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
import static org.mockito.Mockito.when;

public class PolicyManagerServiceFilteringTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private PolicyRepository policyRepository;
    @Mock private RoleRepository roleRepository;

    @Spy private EntityFilterService entityFilterService = new EntityFilterService(Optional.empty());
    @Spy private ThriftAssembler assembler = new ThriftAssembler();
    @InjectMocks private PolicyServiceImpl policyManagerService;

    @Mock private TPolicy tPolicy01;
    @Mock private TPolicy tPolicy02;
    @Mock private TPolicy tPolicy03;

    private PolicyEntity policyEntity1 = new PolicyEntity("policy-1");
    private PolicyEntity policyEntity2 = new PolicyEntity("policy-2");
    private PolicyEntity policyEntity3 = new PolicyEntity("policy-3");
    private RoleEntity roleEntity1 = new RoleEntity("role-1");
    private RoleEntity roleEntity2 = new RoleEntity("role-2");

    private List<EntityFilter> filterServices = new ArrayList<>();

    @Before
    public void setup() {

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

        when(tPolicy01.getId()).thenReturn(1L);
        when(tPolicy02.getId()).thenReturn(2L);
        when(tPolicy03.getId()).thenReturn(3L);

        policyEntity1.addRole(roleEntity1);
        policyEntity2.addRole(roleEntity2);
        policyEntity3.addRole(roleEntity1);
        policyEntity3.addRole(roleEntity2);

        Collection<Policy> policies = asList(policyEntity1, policyEntity2, policyEntity3);
        when(policyRepository.findAll()).thenReturn(policies);
        when(policyRepository.findAllOrderedByName()).thenReturn(policies);

        when(policyRepository.lookUp(1)).thenReturn(policyEntity1);
        when(policyRepository.lookUp(2)).thenReturn(policyEntity2);
        when(policyRepository.lookUp(3)).thenReturn(policyEntity3);

        when(roleRepository.findNotLinked(policyEntity1)).thenReturn(singletonList(roleEntity2));
        when(roleRepository.findNotLinked(policyEntity2)).thenReturn(singletonList(roleEntity1));
        when(roleRepository.findNotLinked(policyEntity3)).thenReturn(emptyList());

        when(roleRepository.findForPolicy(policyEntity1)).thenReturn(singletonList(roleEntity1));
        when(roleRepository.findForPolicy(policyEntity2)).thenReturn(singletonList(roleEntity2));
        when(roleRepository.findForPolicy(policyEntity3)).thenReturn(asList(roleEntity1, roleEntity2));
    }

    @Test
    public void findAll() throws Exception {
        Collection<TPolicy> result = policyManagerService.findAll();

        assertThat(result).extracting(TPolicy::getName).containsExactlyInAnyOrder("policy-1");
    }

    @Test
    public void findRoles() throws Exception {
        assertThat(policyManagerService.findRoles(tPolicy01)).extracting(TRole::getName).containsExactlyInAnyOrder("role-1");
        assertThat(policyManagerService.findRoles(tPolicy02)).isEmpty();
        assertThat(policyManagerService.findRoles(tPolicy03)).extracting(TRole::getName).containsExactlyInAnyOrder("role-1");
    }

    @Test
    public void findRolesNotLinked() throws Exception {
        assertThat(policyManagerService.findRolesNotLinked(tPolicy01)).isEmpty();
        assertThat(policyManagerService.findRolesNotLinked(tPolicy02)).extracting(TRole::getName).containsExactly("role-1");
        assertThat(policyManagerService.findRolesNotLinked(tPolicy03)).isEmpty();
    }

}