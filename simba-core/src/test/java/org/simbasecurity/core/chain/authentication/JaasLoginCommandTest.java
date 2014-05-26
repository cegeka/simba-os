/*
 * Copyright 2013 Simba Open Source
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
 */
package org.simbasecurity.core.chain.authentication;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.audit.AuditMessages.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventCategory;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.CredentialService;

@RunWith(MockitoJUnitRunner.class)
public class JaasLoginCommandTest {

    private static final String USER_NAME = "USER_NAME";
    private static final String LOGIN_MODULE_NAME = "JAAS_LOGIN_TEST";
    private static final String IP_ADDRESS = "IP_ADDRESS";

    @Mock private CredentialService credentialServiceMock;
    @Mock private Audit auditMock;
    @Mock private ChainContext contextMock;

    @Spy AuditLogEventFactory auditLogEventFactory;

    @InjectMocks
    private JaasLoginCommand jaasLoginCommand;

    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);;

    @Before
    public void setUp() throws Exception {
        setupJAAS();

        when(contextMock.getUserName()).thenReturn(USER_NAME);
        when(contextMock.getClientIpAddress()).thenReturn(IP_ADDRESS);

        jaasLoginCommand.setLoginConfEntry(LOGIN_MODULE_NAME);
    }

    @Test
    public void successfulLogin() throws Exception {
        TestLoginModule.setValues(false, true, true, false);

        assertEquals(State.CONTINUE, jaasLoginCommand.execute(contextMock));

        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
    }

    @Test
    public void failedLogin_IncreasesInvalidLoginCount() throws Exception {
        TestLoginModule.setValues(false, true, false, false);

        when(credentialServiceMock.checkUserStatus(USER_NAME, Status.ACTIVE)).thenReturn(true);

        assertEquals(State.FINISH, jaasLoginCommand.execute(contextMock));

        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
        verify(contextMock).redirectWithCredentialError(SimbaMessageKey.LOGIN_FAILED);
        verify(credentialServiceMock).increaseInvalidLoginCountAndBlockAccount(USER_NAME);
    }

    @Test
    public void failedLogin_AuditsWhenAccountBlocked() throws Exception {
        TestLoginModule.setValues(false, true, false, false);

        when(credentialServiceMock.checkUserStatus(USER_NAME, Status.ACTIVE)).thenReturn(true);
        when(credentialServiceMock.increaseInvalidLoginCountAndBlockAccount(USER_NAME)).thenReturn(true);

        assertEquals(State.FINISH, jaasLoginCommand.execute(contextMock));

        verify(auditMock,times(2)).log(captor.capture());
		List<AuditLogEvent> resultAuditLogEvents = captor.getAllValues();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvents.get(0).getCategory());
		assertEquals(FAILURE + JAAS_LOGIN_FAILED, resultAuditLogEvents.get(0).getMessage());
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvents.get(1).getCategory());
		assertEquals(FAILURE + ACCOUNT_BLOCKED, resultAuditLogEvents.get(1).getMessage());
        
        verify(contextMock).redirectWithCredentialError(SimbaMessageKey.LOGIN_FAILED);
        verify(credentialServiceMock).increaseInvalidLoginCountAndBlockAccount(USER_NAME);
    }

    private void setupJAAS() {
        Configuration configurationMock = mock(Configuration.class);

        AppConfigurationEntry entry = new AppConfigurationEntry(TestLoginModule.class.getName(),
                LoginModuleControlFlag.REQUIRED, Collections.<String, Object>emptyMap());

        when(configurationMock.getAppConfigurationEntry(LOGIN_MODULE_NAME)).thenReturn(
                new AppConfigurationEntry[]{entry});

        Configuration.setConfiguration(configurationMock);
    }

    public static class TestLoginModule implements LoginModule {
        private static boolean abort = false;
        private static boolean commit = false;
        private static boolean login = false;
        private static boolean logout = false;

        private static void setValues(boolean abort, boolean commit, boolean login, boolean logout) {
            TestLoginModule.abort = abort;
            TestLoginModule.commit = commit;
            TestLoginModule.login = login;
            TestLoginModule.logout = logout;
        }

        @Override
        public boolean abort() throws LoginException {
            return abort;
        }

        @Override
        public boolean commit() throws LoginException {
            return commit;
        }

        @Override
        public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
                               Map<String, ?> options) {
        }

        @Override
        public boolean login() throws LoginException {
            return login;
        }

        @Override
        public boolean logout() throws LoginException {
            return logout;
        }
    }

}
