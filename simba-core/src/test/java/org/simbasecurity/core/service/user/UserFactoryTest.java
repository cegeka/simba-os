package org.simbasecurity.core.service.user;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.audit.ManagementAudit;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.generator.PasswordGenerator;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.locator.SpringAwareLocator;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.domain.RoleTestBuilder.role;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.domain.UserTestBuilder.aUser;
import static org.simbasecurity.core.domain.user.EmailAddress.email;
import static org.simbasecurity.core.service.communication.reset.password.ResetPasswordReason.NEW_USER;

@RunWith(MockitoJUnitRunner.class)
public class UserFactoryTest {

    @Mock
    private SpringAwareLocator locator;
    @Mock
    private UserValidator userValidator;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ManagementAudit managementAudit;
    @Mock
    private ResetPasswordService resetPasswordService;
    @InjectMocks
    private UserFactory userFactory;

    @Before
    public void setUp() throws Exception {
        GlobalContext.initialize(locator);
        when(locator.locate(UserValidator.class)).thenReturn(userValidator);
    }

    @Test
    public void createUser() throws Exception {
        User user = aDefaultUser().withUserName("userName").build();
        when(userRepository.persist(user)).thenReturn(user);

        User savedUser = userFactory.create(user);

        verify(resetPasswordService).sendResetPasswordMessageTo(user, NEW_USER);
        verify(userRepository).persist(savedUser);
        verify(managementAudit).log("User ''{0}'' created", "userName");
    }

    @Test
    public void cloneUser() throws Exception {
        User userToBeCloned = aDefaultUser().withUserName("userToBeCloned").withRoles(role().name("manager").build()).build();
        when(userRepository.findByName("userToBeCloned")).thenReturn(userToBeCloned);
        User user = aDefaultUser().withUserName("userName").build();
        when(userRepository.persist(user)).thenReturn(user);

        User savedUser = userFactory.cloneUser(user, "userToBeCloned");

        assertThat(savedUser.hasRole("manager")).isTrue();
        verify(userRepository).persist(savedUser);
        verify(managementAudit).log("User ''{0}'' created as clone of ''{1}''", "userName", "userToBeCloned");
    }


    @Test
    public void createUserWithRoles_WillSaveUserAndAddRoles() throws Exception {
        List<String> roleNames = newArrayList("manager", "bediende", "medewerker");
        User user = aDefaultUser().withUserName("userName").build();
        when(roleRepository.findByName("manager")).thenReturn(role().name("manager").build());
        when(roleRepository.findByName("bediende")).thenReturn(role().name("bediende").build());
        when(roleRepository.findByName("medewerker")).thenReturn(role().name("medewerker").build());
        when(userRepository.persist(user)).thenReturn(user);

        User savedUser = userFactory.createWithRoles(user, roleNames);

        assertThat(savedUser.hasRole("manager")).isTrue();
        assertThat(savedUser.hasRole("bediende")).isTrue();
        assertThat(savedUser.hasRole("medewerker")).isTrue();

        verify(userRepository).persist(user);
        verify(managementAudit).log("User ''{0}'' created with roles ''{1}''", "userName", "manager, bediende, medewerker");
    }

    @Test
    public void createEIDUserWithRoles() throws Exception {
        List<String> roleNames = newArrayList("manager", "bediende", "medewerker");
        User user = aDefaultUser().withUserName("userName").build();
        when(roleRepository.findByName("manager")).thenReturn(role().name("manager").build());
        when(roleRepository.findByName("bediende")).thenReturn(role().name("bediende").build());
        when(roleRepository.findByName("medewerker")).thenReturn(role().name("medewerker").build());
        when(userRepository.persist(user)).thenReturn(user);

        User savedUser = userFactory.createEIDUserWithRoles(user, roleNames);

        assertThat(savedUser.hasRole("manager")).isTrue();
        assertThat(savedUser.hasRole("bediende")).isTrue();
        assertThat(savedUser.hasRole("medewerker")).isTrue();

        verify(userRepository).persist(user);
        verify(managementAudit).log("User ''{0}'' created with roles ''{1}''", "userName", "manager, bediende, medewerker");
    }

    @Test
    public void createRestUser_WillCreateUserWithGeneratedPassword() throws Exception {
        User userMock = mock(User.class);
        when(userRepository.persist(any(User.class))).thenReturn(userMock);
        when(passwordGenerator.generatePassword()).thenReturn("someGeneratedPassword");

        userFactory.createRestUser("userName");

        verify(userMock).changePassword("someGeneratedPassword", "someGeneratedPassword");
    }

    @Test
    public void createUserWithExistingUserName_ThrowsError() throws Exception {
        User user = aUser().withUserName("userName").build();
        when(userRepository.findByName("userName")).thenReturn(aDefaultUser().build());

        Assertions.assertThatThrownBy(() -> userFactory.create(user))
                .isInstanceOf(SimbaException.class)
                .hasMessage("User already exists with username: userName");
    }

    @Test
    public void createUserWithExistingEmail_ThrowsError() throws Exception {
        User user = aUser().withEmail("someEmail@email.com").build();
        when(userRepository.findByEmail(email("someEmail@email.com"))).thenReturn(aDefaultUser().build());

        Assertions.assertThatThrownBy(() -> userFactory.create(user))
                .isInstanceOf(SimbaException.class)
                .hasMessage("User already exists with email: someEmail@email.com");
    }
}