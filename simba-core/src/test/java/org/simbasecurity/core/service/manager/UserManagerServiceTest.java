package org.simbasecurity.core.service.manager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.domain.PolicyEntity;
import org.simbasecurity.core.domain.RoleEntity;
import org.simbasecurity.core.domain.UserEntity;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.locator.SpringAwareLocator;
import org.simbasecurity.core.service.manager.dto.PolicyDTO;
import org.simbasecurity.core.service.manager.dto.RoleDTO;
import org.simbasecurity.core.service.manager.dto.UserDTO;
import org.simbasecurity.test.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class UserManagerServiceTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private SpringAwareLocator locator;
    @Mock private UserValidator userValidator;
    @Mock private PasswordValidator passwordValidator;
    @Mock private ConfigurationService configurationService;

    @Mock private PolicyRepository policyRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRepository userRepository;

    @Spy private EntityFilterService entityFilterService = new EntityFilterService(Optional.empty());
    @InjectMocks private UserManagerService service;

    @Mock private RoleDTO roleDTO1;
    @Mock private RoleDTO roleDTO2;

    @Mock private UserDTO userDTO1;
    @Mock private UserDTO userDTO2;
    @Mock private UserDTO userDTO3;

    private PolicyEntity policyEntity1 = new PolicyEntity("policy-1");
    private PolicyEntity policyEntity2 = new PolicyEntity("policy-2");
    private RoleEntity roleEntity1 = new RoleEntity("role-1");
    private RoleEntity roleEntity2 = new RoleEntity("role-2");

    private List<EntityFilter> filterServices = new ArrayList<>();

    @Before
    public void setup() {
        GlobalContext.initialize(locator);
        when(locator.locate(UserValidator.class)).thenReturn(userValidator);
        when(locator.locate(PasswordValidator.class)).thenReturn(passwordValidator);
        when(locator.locate(ConfigurationService.class)).thenReturn(configurationService);

        UserEntity userEntity1 = new UserEntity("user-1");
        UserEntity userEntity2 = new UserEntity("user-2");
        UserEntity userEntity3 = new UserEntity("user-3");

        ReflectionUtil.setField(entityFilterService, "filters", filterServices);

        entityFilterService.initializePredicates();

        userEntity1.addRole(roleEntity1);
        userEntity2.addRole(roleEntity2);
        userEntity3.addRoles(asList(roleEntity1, roleEntity2));

        roleEntity1.addPolicy(policyEntity1);
        roleEntity2.addPolicy(policyEntity2);

        when(userRepository.findAll()).thenReturn(asList(userEntity1, userEntity2, userEntity3));

        when(userRepository.lookUp(userDTO1)).thenReturn(userEntity1);
        when(userRepository.lookUp(userDTO2)).thenReturn(userEntity2);
        when(userRepository.lookUp(userDTO3)).thenReturn(userEntity3);

        when(roleRepository.lookUp(roleDTO1)).thenReturn(roleEntity1);
        when(roleRepository.lookUp(roleDTO2)).thenReturn(roleEntity2);

        when(roleRepository.findNotLinked(userEntity1)).thenReturn(singletonList(roleEntity2));
        when(roleRepository.findNotLinked(userEntity2)).thenReturn(singletonList(roleEntity1));
        when(roleRepository.findNotLinked(userEntity3)).thenReturn(emptyList());

        when(policyRepository.find(userEntity1)).thenReturn(singletonList(policyEntity1));
        when(policyRepository.find(userEntity2)).thenReturn(singletonList(policyEntity2));
        when(policyRepository.find(userEntity3)).thenReturn(asList(policyEntity1, policyEntity2));
    }

    @Test
    public void findAll() {
        assertThat(service.findAll()).extracting(UserDTO::getUserName).containsExactlyInAnyOrder("user-1", "user-2", "user-3");
    }

    @Test
    public void find() {
        assertThat(service.find(roleDTO1)).extracting(UserDTO::getUserName).containsExactlyInAnyOrder("user-1", "user-3");
        assertThat(service.find(roleDTO2)).extracting(UserDTO::getUserName).containsExactlyInAnyOrder("user-2", "user-3");
    }

    @Test
    public void findRoles() {
        assertThat(service.findRoles(userDTO1)).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-1");
        assertThat(service.findRoles(userDTO2)).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-2");
        assertThat(service.findRoles(userDTO3)).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-1", "role-2");
    }

    @Test
    public void findRolesNotLinked() {
        assertThat(service.findRolesNotLinked(userDTO1)).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-2");
        assertThat(service.findRolesNotLinked(userDTO2)).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-1");
        assertThat(service.findRolesNotLinked(userDTO3)).extracting(RoleDTO::getName).isEmpty();
    }

    @Test
    public void findPolicies() {
        assertThat(service.findPolicies(userDTO1)).extracting(PolicyDTO::getName).containsExactlyInAnyOrder("policy-1");
        assertThat(service.findPolicies(userDTO2)).extracting(PolicyDTO::getName).containsExactlyInAnyOrder("policy-2");
        assertThat(service.findPolicies(userDTO3)).extracting(PolicyDTO::getName).containsExactlyInAnyOrder("policy-1", "policy-2");

    }
}
