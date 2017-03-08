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
import org.simbasecurity.core.domain.Role;
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
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class RoleManagerServiceTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private SpringAwareLocator locator;
    @Mock private UserValidator userValidator;
    @Mock private PasswordValidator passwordValidator;
    @Mock private ConfigurationService configurationService;

    @Mock private PolicyRepository policyRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRepository userRepository;

    @Spy private EntityFilterService entityFilterService;
    @InjectMocks private RoleManagerService roleManagerService;

    @Mock private RoleDTO roleDTO1;
    @Mock private RoleDTO roleDTO2;
    @Mock private RoleDTO roleDTO3;

    private PolicyEntity policyEntity1 = new PolicyEntity("policy-1");
    private PolicyEntity policyEntity2 = new PolicyEntity("policy-2");
    private RoleEntity roleEntity1 = new RoleEntity("role-1");
    private RoleEntity roleEntity2 = new RoleEntity("role-2");
    private RoleEntity roleEntity3 = new RoleEntity("role-3");

    private List<EntityFilter> filterServices = new ArrayList<>();

    @Before
    public void setup() {
        GlobalContext.initialize(locator);
        when(locator.locate(UserValidator.class)).thenReturn(userValidator);
        when(locator.locate(PasswordValidator.class)).thenReturn(passwordValidator);
        when(locator.locate(ConfigurationService.class)).thenReturn(configurationService);

        UserEntity userEntity1 = new UserEntity("user-1");
        UserEntity userEntity2 = new UserEntity("user-2");

        ReflectionUtil.setField(entityFilterService, "filters", filterServices);

        entityFilterService.initializePredicates();

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

        when(roleRepository.lookUp(roleDTO1)).thenReturn(roleEntity1);
        when(roleRepository.lookUp(roleDTO2)).thenReturn(roleEntity2);
        when(roleRepository.lookUp(roleDTO3)).thenReturn(roleEntity3);

        when(policyRepository.findNotLinked(roleEntity1)).thenReturn(singletonList(policyEntity2));
        when(policyRepository.findNotLinked(roleEntity2)).thenReturn(singletonList(policyEntity1));
        when(policyRepository.findNotLinked(roleEntity3)).thenReturn(emptyList());

        when(userRepository.findNotLinked(roleEntity1)).thenReturn(singletonList(userEntity2));
        when(userRepository.findNotLinked(roleEntity2)).thenReturn(singletonList(userEntity1));
        when(userRepository.findNotLinked(roleEntity3)).thenReturn(emptyList());
    }

    @Test
    public void findAll() throws Exception {
        assertThat(roleManagerService.findAll()).extracting(RoleDTO::getName).containsExactlyInAnyOrder("role-1", "role-2", "role-3");
    }

    @Test
    public void findPolicies() throws Exception {
        assertThat(roleManagerService.findPolicies(roleDTO1)).extracting(PolicyDTO::getName).containsExactly("policy-1");
        assertThat(roleManagerService.findPolicies(roleDTO2)).extracting(PolicyDTO::getName).containsExactly("policy-2");
        assertThat(roleManagerService.findPolicies(roleDTO3)).extracting(PolicyDTO::getName).containsExactly("policy-1", "policy-2");
    }

    @Test
    public void findPoliciesNotLinked() throws Exception {
        assertThat(roleManagerService.findPoliciesNotLinked(roleDTO1)).extracting(PolicyDTO::getName).containsExactly("policy-2");
        assertThat(roleManagerService.findPoliciesNotLinked(roleDTO2)).extracting(PolicyDTO::getName).containsExactly("policy-1");
        assertThat(roleManagerService.findPoliciesNotLinked(roleDTO3)).extracting(PolicyDTO::getName).isEmpty();
    }

    @Test
    public void findUsers() throws Exception {
        assertThat(roleManagerService.findUsers(roleDTO1)).extracting(UserDTO::getUserName).containsExactly("user-1");
        assertThat(roleManagerService.findUsers(roleDTO2)).extracting(UserDTO::getUserName).containsExactly("user-2");
        assertThat(roleManagerService.findUsers(roleDTO3)).extracting(UserDTO::getUserName).containsExactly("user-1", "user-2");
    }

    @Test
    public void findUsersNotLinked() throws Exception {
        assertThat(roleManagerService.findUsersNotLinked(roleDTO1)).extracting(UserDTO::getUserName).containsExactly("user-2");
        assertThat(roleManagerService.findUsersNotLinked(roleDTO2)).extracting(UserDTO::getUserName).containsExactly("user-1");
        assertThat(roleManagerService.findUsersNotLinked(roleDTO3)).extracting(UserDTO::getUserName).isEmpty();
    }
}