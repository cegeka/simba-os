package org.simbasecurity.core.chain.eid;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserEntity;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.service.UserService;
import org.simbasecurity.test.LocatorTestCase;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateEIDUserCommandTest extends LocatorTestCase {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    private static final String INSZ = "insz";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String EMAIL = "email";
    private static final String NL = "nl";

    @Mock private UserService userServiceMock;
    @Mock private ConfigurationService configurationServiceMock;

    @Mock private ChainContext chainContextMock;
    @Mock private User userMock;
    @Mock private Audit auditMock;
    @Mock private AuditLogEventFactory auditLogEventFactoryMock;

    @InjectMocks private CreateEIDUserCommand createEIDUserCommand;

    @Captor private ArgumentCaptor<User> userCaptor;
    @Captor private ArgumentCaptor<List<String>> roleListCaptor;

    private SAMLUser samlUser = new SAMLUser(INSZ, FIRSTNAME, LASTNAME, EMAIL, NL);

    @Before
    public void setUp() {
        implantMock(UserValidator.class);
        implantMock(PasswordValidator.class);

        implant(ConfigurationService.class, configurationServiceMock);
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.DEFAULT_USER_ROLE)).thenReturn(Collections.singletonList("guest"));
    }

    @Test
    public void execute_CreateNewUser() throws Exception {
        when(chainContextMock.getSAMLUser()).thenReturn(samlUser);
        when(userServiceMock.findByName(INSZ)).thenReturn(null);

        State state = createEIDUserCommand.execute(chainContextMock);

        assertEquals(State.CONTINUE, state);

        verify(userServiceMock).create(userCaptor.capture(), roleListCaptor.capture());
        verify(chainContextMock).setUserPrincipal(INSZ);

        User user = userCaptor.getValue();
        assertEquals(INSZ, user.getUserName());
        assertEquals(FIRSTNAME, user.getFirstName());
        assertEquals(LASTNAME, user.getName());
        assertEquals(Language.nl_NL, user.getLanguage());
        assertFalse(user.isChangePasswordOnNextLogon());
        assertFalse(user.isPasswordChangeRequired());

        List<String> roleList = roleListCaptor.getValue();
        assertEquals(1, roleList.size());
        assertEquals("guest", roleList.iterator().next());
    }

    @Test
    public void execute_UpdateExistingUser() throws Exception {
        User user = new UserEntity(INSZ);

        when(chainContextMock.getSAMLUser()).thenReturn(samlUser);
        when(userServiceMock.findByName(INSZ)).thenReturn(user);

        State state = createEIDUserCommand.execute(chainContextMock);

        assertEquals(State.CONTINUE, state);

        verify(chainContextMock).setUserPrincipal(INSZ);

        assertEquals(INSZ, user.getUserName());
        assertEquals(FIRSTNAME, user.getFirstName());
        assertEquals(LASTNAME, user.getName());
        assertEquals(Language.fromISO639Code(NL), user.getLanguage());
    }
}