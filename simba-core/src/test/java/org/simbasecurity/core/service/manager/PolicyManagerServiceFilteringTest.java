package org.simbasecurity.core.service.manager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.domain.*;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.service.manager.dto.PolicyDTO;
import org.simbasecurity.core.service.manager.dto.RoleDTO;
import org.simbasecurity.test.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
    @InjectMocks private PolicyManagerService policyManagerService;

    @Mock private PolicyDTO policyDTO1;
    @Mock private PolicyDTO policyDTO2;
    @Mock private PolicyDTO policyDTO3;

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
            public Predicate<Role> rolePredicate() {
                return r -> r.getName().endsWith("-1");
            }

            @Override
            public Predicate<Policy> policyPredicate() {
                return p -> p.getName().endsWith("-1");
            }

            @Override
            public Predicate<User> userPredicate() {
                return u -> u.getUserName().endsWith("-1");
            }
        });

        ReflectionUtil.setField(entityFilterService, "filters", filterServices);

        entityFilterService.initializePredicates();

        policyEntity1.addRole(roleEntity1);
        policyEntity2.addRole(roleEntity2);
        policyEntity3.addRole(roleEntity1);
        policyEntity3.addRole(roleEntity2);

        Collection<Policy> policies = asList(policyEntity1, policyEntity2, policyEntity3);
        when(policyRepository.findAll()).thenReturn(policies);

        when(policyRepository.lookUp(policyDTO1)).thenReturn(policyEntity1);
        when(policyRepository.lookUp(policyDTO2)).thenReturn(policyEntity2);
        when(policyRepository.lookUp(policyDTO3)).thenReturn(policyEntity3);

        when(roleRepository.findNotLinked(policyEntity1)).thenReturn(singletonList(roleEntity2));
        when(roleRepository.findNotLinked(policyEntity2)).thenReturn(singletonList(roleEntity1));
        when(roleRepository.findNotLinked(policyEntity3)).thenReturn(emptyList());

        when(roleRepository.findForPolicy(policyEntity1)).thenReturn(singletonList(roleEntity1));
        when(roleRepository.findForPolicy(policyEntity2)).thenReturn(singletonList(roleEntity2));
        when(roleRepository.findForPolicy(policyEntity3)).thenReturn(asList(roleEntity1, roleEntity2));
    }

    @Test
    public void findAll() throws Exception {
        Collection<PolicyDTO> result = policyManagerService.findAll();

        assertThat(result).extracting(PolicyDTO::getName).containsExactlyInAnyOrder("policy-1");
    }

    @Test
    public void findRoles() throws Exception {
        assertThat(policyManagerService.findRoles(policyDTO1)).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-1");
        assertThat(policyManagerService.findRoles(policyDTO2)).isEmpty();
        assertThat(policyManagerService.findRoles(policyDTO3)).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-1");
    }

    @Test
    public void findRolesNotLinked() throws Exception {
        assertThat(policyManagerService.findRolesNotLinked(policyDTO1)).isEmpty();
        assertThat(policyManagerService.findRolesNotLinked(policyDTO2)).extracting(RoleDTO::getName).containsExactly("role-1");
        assertThat(policyManagerService.findRolesNotLinked(policyDTO3)).isEmpty();
    }

}