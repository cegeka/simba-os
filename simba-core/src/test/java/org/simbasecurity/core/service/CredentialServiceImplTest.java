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

import org.apache.commons.lang.time.DateUtils;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.ManagementAudit;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

public class CredentialServiceImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String DEFAULT_PASSWORD = "Simba3D";

    private static final String PASSWORD = "password";

    private static final String USERNAME = "username";
    private static final String OTHER_USER_NAME = "otherUser";

    private static final int PASSWORD_EXPIRATION_TIME = 90;

    @Mock private UserRepository mockUserRepository;
    @Mock private ConfigurationServiceImpl mockConfigurationService;
    @Mock private Audit mockAudit;
    @Mock private ManagementAudit managementAudit;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks private CredentialServiceImpl credentialService;

    @Test
    public void testChangePasswordAuthorized() {
        String newPassword = "Newnew$";

        User user = mock(User.class);
        when(user.changePasswordAuthorized(DEFAULT_PASSWORD, newPassword)).thenReturn(true);
        when(mockUserRepository.findByName(USERNAME)).thenReturn(user);

        boolean result = credentialService.changePasswordAuthorized(USERNAME, DEFAULT_PASSWORD, newPassword);

        assertTrue(result);

    }

    @Test(expected = SimbaException.class)
    public void testChangePasswordAuthorized_userDoesntExist_IllegalArgumentException() {
        String oldPassword = "old";
        String newPassword = "new";

        when(mockUserRepository.findByName(USERNAME)).thenReturn(null);

        credentialService.changePasswordAuthorized(USERNAME, oldPassword, newPassword);
    }

    @Test
    public void checkActiveCredentials() {
        User user = mock(User.class);
        when(user.checkPassword(PASSWORD)).thenReturn(true);
        when(mockUserRepository.findByName(USERNAME)).thenReturn(user);

        assertTrue(credentialService.checkCredentials(USERNAME, PASSWORD));
    }

    @Test
    public void checkActiveCredentials_UserNotFound() {
        when(mockUserRepository.findByName(USERNAME)).thenReturn(null);

        assertFalse(credentialService.checkCredentials(USERNAME, PASSWORD));
    }

    @Test(expected = SimbaException.class)
    public void checkUserStatus_throwsSimbaExceptionIfUserDoesntExist() {
        when(mockUserRepository.findByName(USERNAME)).thenReturn(null);
        credentialService.checkUserStatus(USERNAME, Status.ACTIVE);
    }

    @Test
    public void checkUserStatus() {
        User user = mock(User.class);
        when(user.getStatus()).thenReturn(Status.ACTIVE);

        User blockedUser = mock(User.class);
        when(blockedUser.getStatus()).thenReturn(Status.BLOCKED);

        when(mockUserRepository.findByName(USERNAME)).thenReturn(user);
        when(mockUserRepository.findByName(OTHER_USER_NAME)).thenReturn(blockedUser);

        assertFalse(credentialService.checkUserStatus(USERNAME, Status.BLOCKED));
        assertTrue(credentialService.checkUserStatus(OTHER_USER_NAME, Status.BLOCKED));
    }

    @Test
    public void markUsersForPasswordChange() {
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

        Date notLongerThenChangeRateAgo = DateUtils.addDays(today, -PASSWORD_EXPIRATION_TIME);
        Date longerThenChangeRateAgo = DateUtils.addDays(today, -(PASSWORD_EXPIRATION_TIME + 1));

        User userWithExpiredPassword = mock(User.class);
        User userWithExpiredPasswordButNotRequired = mock(User.class);
        User userWithValidPassword = mock(User.class);

        when(userWithExpiredPassword.isPasswordChangeRequired()).thenReturn(true);
        when(userWithExpiredPasswordButNotRequired.isPasswordChangeRequired()).thenReturn(false);
        when(userWithValidPassword.isPasswordChangeRequired()).thenReturn(true);

        when(userWithExpiredPassword.getDateOfLastPasswordChange()).thenReturn(longerThenChangeRateAgo);
        when(userWithExpiredPasswordButNotRequired.getDateOfLastPasswordChange()).thenReturn(longerThenChangeRateAgo);
        when(userWithValidPassword.getDateOfLastPasswordChange()).thenReturn(notLongerThenChangeRateAgo);

        when(mockConfigurationService.getValue(SimbaConfigurationParameter.PASSWORD_LIFE_TIME)).thenReturn(
                PASSWORD_EXPIRATION_TIME);
        when(mockUserRepository.findAll()).thenReturn(
                Arrays.asList(userWithExpiredPassword, userWithExpiredPasswordButNotRequired, userWithValidPassword));

        credentialService.markUsersForPasswordChange();

        verify(userWithExpiredPassword).setChangePasswordOnNextLogon(true);

        verify(userWithExpiredPasswordButNotRequired, never()).setChangePasswordOnNextLogon(anyBoolean());
        verify(userWithValidPassword, never()).setChangePasswordOnNextLogon(anyBoolean());
    }
}
