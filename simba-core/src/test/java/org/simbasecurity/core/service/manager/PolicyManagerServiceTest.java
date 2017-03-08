package org.simbasecurity.core.service.manager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.PolicyEntity;
import org.simbasecurity.core.domain.RoleEntity;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.service.manager.dto.PolicyDTO;
import org.simbasecurity.core.service.manager.dto.RoleDTO;
import org.simbasecurity.test.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PolicyManagerServiceTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private PolicyRepository policyRepository;
    @Mock private RoleRepository roleRepository;

    @Spy private EntityFilterService entityFilterService;
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

    }

    @Test
    public void findAll() throws Exception {
        assertThat(policyManagerService.findAll()).extracting(PolicyDTO::getName).containsExactlyInAnyOrder("policy-1", "policy-2", "policy-3");
    }

    @Test
    public void findRoles() throws Exception {
        assertThat(policyManagerService.findRoles(policyDTO1)).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-1");
        assertThat(policyManagerService.findRoles(policyDTO2)).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-2");
        assertThat(policyManagerService.findRoles(policyDTO3)).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-1", "role-2");
    }

    @Test
    public void findRolesNotLinked() throws Exception {
        assertThat(policyManagerService.findRolesNotLinked(policyDTO1)).extracting(RoleDTO::getName).containsExactly("role-2");
        assertThat(policyManagerService.findRolesNotLinked(policyDTO2)).extracting(RoleDTO::getName).containsExactly("role-1");
        assertThat(policyManagerService.findRolesNotLinked(policyDTO3)).isEmpty();
    }
}