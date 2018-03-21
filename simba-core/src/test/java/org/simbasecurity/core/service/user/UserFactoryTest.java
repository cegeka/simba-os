package org.simbasecurity.core.service.user;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.audit.ManagementAudit;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.StubEmailFactory;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.generator.PasswordGenerator;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.communication.reset.password.NewUser;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordService;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.AutowirerRule;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.domain.RoleTestBuilder.role;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.domain.UserTestBuilder.aUser;
import static org.simbasecurity.core.exception.SimbaMessageKey.EMAIL_ADDRESS_REQUIRED;

@RunWith(MockitoJUnitRunner.class)
public class UserFactoryTest {

    @Rule public AutowirerRule autowirerRule = AutowirerRule.autowirer();

    @Mock private RoleRepository roleRepository;
    @Mock private PasswordGenerator passwordGenerator;
    @Mock private UserRepository userRepository;
    @Mock private ManagementAudit managementAudit;
    @Mock private ResetPasswordService resetPasswordService;
    @Mock private NewUser newUserReason;


    @InjectMocks
    private UserFactory userFactory;

    private StubEmailFactory emailFactory = StubEmailFactory.emailRequired();
    private CoreConfigurationService configurationService = emailFactory.configurationService();

    @Before
    public void setUp() {
        autowirerRule.mockBean(UserValidator.class);
        autowirerRule.mockBean(PasswordValidator.class);
        autowirerRule.registerBean(emailFactory);

        userFactory.setConfigurationService(configurationService);
    }

    @Test
    public void createUserWithoutEmail_EmailNotRequiredAccordingToParameter_ShouldNotThrowAnException() {
        User user = aDefaultUser().withoutEmail().withUserName("userName").build();
        when(userRepository.persist(user)).thenReturn(user);
        when(configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);

        User savedUser = userFactory.create(user);

        verifyZeroInteractions(resetPasswordService);
        verify(userRepository).persist(savedUser);
        verify(managementAudit).log("User ''{0}'' created", "userName");
    }

    @Test
    public void createUser_UserWithEmail_EmailNotRequiredAccordingToParameter_ShouldNotThrowAnException() {
        User user = aDefaultUser().withEmail("joker@acme.com").withUserName("userName").build();
        when(userRepository.persist(user)).thenReturn(user);
        when(configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);

        User savedUser = userFactory.create(user);

        verify(resetPasswordService).sendResetPasswordMessageTo(user, newUserReason);
        verify(userRepository).persist(savedUser);
        verify(managementAudit).log("User ''{0}'' created", "userName");
    }

    @Test
    public void createUser_UserWithoutEmail_EmailRequiredAccordingToParameter_ShouldThrowAnException() {
        User user = aDefaultUser().withoutEmail().withUserName("userName").build();
        when(configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(true);

        assertThatThrownBy(() -> userFactory.create(user))
                .extracting(t -> ((SimbaException) t).getMessageKey())
                .containsExactly(EMAIL_ADDRESS_REQUIRED);

        verifyZeroInteractions(resetPasswordService);
        verify(userRepository, never()).persist(any(User.class));
        verify(managementAudit, never()).log(any(String.class), any(String.class));
    }

    @Test
    public void cloneUser_UserHasEmail_EmailRequiredAccordingToParameter_ShouldNotThrownAnException() {
        User userBeingCreated = aDefaultUser()
                .withEmail("johnnytampony@gmail.com")
                .withUserName("johnnyTampony")
                .build();
        User userToBeCloned = aDefaultUser()
                .withEmail("snarf@thundercats.com")
                .withUserName("userToBeCloned")
                .withRoles(role()
                        .name("manager")
                        .build())
                .build();
        when(userRepository.findByName("userToBeCloned")).thenReturn(userToBeCloned);
        when(userRepository.persist(userBeingCreated)).thenReturn(userBeingCreated);
        when(configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(true);

        User savedUser = userFactory.cloneUser(userBeingCreated, "userToBeCloned");

        assertThat(savedUser.hasRole("manager")).isTrue();
        assertThat(savedUser.getEmail()).isEqualTo(emailFactory.email("johnnytampony@gmail.com"));

        verify(userRepository).persist(savedUser);
        verify(managementAudit).log("User ''{0}'' created as clone of ''{1}''", "johnnyTampony", "userToBeCloned");
    }

    @Test
    public void cloneUser_UserWithoutEmail_EmailNotRequiredAccordingToParameter_ShouldNotThrowError() {
        User userBeingCreated = aDefaultUser()
                .withoutEmail()
                .withUserName("johnnyTampony")
                .build();
        User userToBeCloned = aDefaultUser()
                .withoutEmail()
                .withUserName("userToBeCloned")
                .withRoles(role()
                        .name("manager")
                        .build())
                .build();
        when(userRepository.findByName("userToBeCloned")).thenReturn(userToBeCloned);
        when(userRepository.persist(userBeingCreated)).thenReturn(userBeingCreated);
        when(configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);

        User savedUser = userFactory.cloneUser(userBeingCreated, "userToBeCloned");

        assertThat(savedUser.hasRole("manager")).isTrue();
        assertThat(savedUser.getEmail().isEmpty()).isTrue();

        verify(userRepository).persist(savedUser);
        verify(managementAudit).log("User ''{0}'' created as clone of ''{1}''", "johnnyTampony", "userToBeCloned");
    }

    @Test
    public void createUserWithRoles_WillSaveUserAndAddRoles() {
        List<String> roleNames = newArrayList("manager", "bediende", "medewerker");
        User user = aDefaultUser().withUserName("userName").build();
        when(roleRepository.findByName("manager")).thenReturn(role().name("manager").build());
        when(roleRepository.findByName("bediende")).thenReturn(role().name("bediende").build());
        when(roleRepository.findByName("medewerker")).thenReturn(role().name("medewerker").build());
        when(configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(true);
        when(userRepository.persist(user)).thenReturn(user);

        User savedUser = userFactory.createWithRoles(user, roleNames);

        assertThat(savedUser.hasRole("manager")).isTrue();
        assertThat(savedUser.hasRole("bediende")).isTrue();
        assertThat(savedUser.hasRole("medewerker")).isTrue();

        verify(userRepository).persist(user);
        verify(managementAudit).log("User ''{0}'' created with roles ''{1}''", "userName", "manager, bediende, medewerker");
    }

    @Test
    public void createEIDUserWithRoles() {
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
    public void createRestUser_WillCreateUserWithGeneratedPassword() {
        User userMock = mock(User.class);
        when(userRepository.persist(any(User.class))).thenReturn(userMock);
        when(passwordGenerator.generatePassword()).thenReturn("someGeneratedPassword");

        userFactory.createRestUser("userName");

        verify(userMock).changePassword("someGeneratedPassword", "someGeneratedPassword");
    }

    @Test
    public void createUser_WithExistingUserName_ThrowsError() {
        User user = aUser().withUserName("userName").build();
        User aDefaultUser= aDefaultUser().build();
        when(userRepository.findByName("userName")).thenReturn(aDefaultUser);

        assertThatThrownBy(() -> userFactory.create(user))
                .isInstanceOf(SimbaException.class)
                .hasMessage("User already exists with username: userName");
    }

    @Test
    public void createUser_WithExistingEmail_ThrowsError() {
        User user = aUser().withEmail("someEmail@email.com").build();
        User aDefaultUser= aDefaultUser().build();
        when(userRepository.findByEmail(emailFactory.email("someEmail@email.com"))).thenReturn(aDefaultUser);

        assertThatThrownBy(() -> userFactory.create(user))
                .isInstanceOf(SimbaException.class)
                .hasMessage("User already exists with email: someEmail@email.com");
    }

    @Test
    public void cloneUser_WithExistingUserName_ThrowsError() {
        User user = aDefaultUser().withUserName("userName").build();
        User aDefaultUser = aDefaultUser().build();
        when(userRepository.findByName("userName")).thenReturn(aDefaultUser);

        assertThatThrownBy(() -> userFactory.cloneUser(user, "username"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("User already exists with username: userName");
    }

    @Test
    public void cloneUser_WithExistingEmail_ThrowsError() {
        User user = aDefaultUser().withUserName("stormTrooper").withEmail("theOneAndOnlyStormTrooper@empire.com").build();
        User aDefaultUser= aDefaultUser().build();
        when(userRepository.findByEmail(emailFactory.email("theOneAndOnlyStormTrooper@empire.com"))).thenReturn(aDefaultUser);

        assertThatThrownBy(() -> userFactory.cloneUser(user, "Finn"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("User already exists with email: theOneAndOnlyStormTrooper@empire.com");
    }
}