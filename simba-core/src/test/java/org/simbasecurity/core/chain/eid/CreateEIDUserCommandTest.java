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
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.StubEmailFactory;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.service.UserService;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.core.service.user.UserFactory;
import org.simbasecurity.test.AutowirerRule;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;

public class CreateEIDUserCommandTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    @Rule public AutowirerRule autowirerRule = AutowirerRule.autowirer();

    private static final String INSZ = "insz";
    private static final String FIRSTNAME = "johnny";
    private static final String LASTNAME = "tampony";
    private static final String EMAIL = "johnny.tampony@hotmail.com";
    private static final String NL = "nl";

    @Mock private UserService userServiceMock;
    @Mock private UserFactory userFactoryMock;

    @Mock private ChainContext chainContextMock;
    @Mock private User userMock;
    @Mock private Audit auditMock;
    @Mock private AuditLogEventFactory auditLogEventFactoryMock;

    @InjectMocks private CreateEIDUserCommand createEIDUserCommand;

    private CoreConfigurationService configurationServiceMock;

    @Captor private ArgumentCaptor<User> userCaptor;
    @Captor private ArgumentCaptor<List<String>> roleListCaptor;

    private SAMLUser samlUser = new SAMLUser(INSZ, FIRSTNAME, LASTNAME, EMAIL, NL);

    @Before
    public void setUp() {
        autowirerRule.mockBean(UserValidator.class);
        autowirerRule.mockBean(PasswordValidator.class);
        autowirerRule.registerBean(StubEmailFactory.emailRequired());

        configurationServiceMock = mock(CoreConfigurationService.class);
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.DEFAULT_USER_ROLE)).thenReturn(Collections.singletonList("guest"));

        createEIDUserCommand.setConfigurationService(configurationServiceMock);
    }

    @Test
    public void execute_CreateNewUser() throws Exception {
        when(chainContextMock.getSAMLUser()).thenReturn(samlUser);
        when(userServiceMock.findByName(INSZ)).thenReturn(null);

        State state = createEIDUserCommand.execute(chainContextMock);

        assertEquals(State.CONTINUE, state);

        verify(userFactoryMock).createEIDUserWithRoles(userCaptor.capture(), roleListCaptor.capture());
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
        User user = aDefaultUser().withUserName(INSZ).build();

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