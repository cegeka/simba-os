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

package org.simbasecurity.core.service.manager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.SessionRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.service.ErrorSender;
import org.simbasecurity.core.service.manager.dto.ChangePasswordDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class UserManagerServiceChangePasswordTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private SessionRepository sessionRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private UserManagerService service;

    private ChangePasswordDTO changePasswordDTO;

    @Mock private User correspondingUser;

    private SSOToken ssoToken = new SSOToken("56161616");
    private String newPassword = "newBlaBla1";
    private String newPasswordConfirmation = "newBlaBla1";

    @Mock private HttpServletResponse responseMock;

    @Before
    public void setup() {

        changePasswordDTO = new ChangePasswordDTO();
        String userName = "user1";
        changePasswordDTO.setUserName(userName);
        changePasswordDTO.setNewPassword(newPassword);
        changePasswordDTO.setNewPasswordConfirmation(newPasswordConfirmation);

        Session aSession = mock(Session.class);

        when(sessionRepository.findBySSOToken(ssoToken)).thenReturn(aSession);

        when(userRepository.findByName(userName)).thenReturn(correspondingUser);
    }

    @Test
    public void changeUserPasswordOk_headerToken() {

        service.changeUserPassword(ssoToken.getToken(), null, changePasswordDTO, responseMock);

        verify(correspondingUser).changePassword(newPassword, newPasswordConfirmation);
        verifyZeroInteractions(responseMock);
    }

    @Test
    public void changeUserPasswordOk_cookieToken() {

        service.changeUserPassword(null, ssoToken.getToken(), changePasswordDTO, responseMock);

        verify(correspondingUser).changePassword(newPassword, newPasswordConfirmation);
        verifyZeroInteractions(responseMock);
    }

    @Test
    public void changeUserPasswordNotOk_noSsoToken_unauthorized() throws IOException {

        service.changeUserPassword(null, null, changePasswordDTO, responseMock);

        assertPasswordNotChanged();
        assertUnauthorizedError();
    }

    @Test
    public void changeUserPasswordNotOk_noActiveSession_unauthorized() throws IOException {

        when(sessionRepository.findBySSOToken(ssoToken)).thenReturn(null);
        service.changeUserPassword(ssoToken.getToken(), null, changePasswordDTO, responseMock);

        assertPasswordNotChanged();
        assertUnauthorizedError();
    }

    @Test
    public void changeUserPasswordNotOk_userNotFound() throws IOException {

        changePasswordDTO.setUserName("someUnknownUser");
        service.changeUserPassword(ssoToken.getToken(), null, changePasswordDTO, responseMock);

        assertPasswordNotChanged();
        assertUserNotFoundError();
    }

    private void assertUnauthorizedError() throws IOException {
        verify(responseMock).sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
    }

    private void assertUserNotFoundError() throws IOException {
        verify(responseMock).sendError(ErrorSender.NO_USER_FOUND_ERROR_CODE, "User with user name 'someUnknownUser' not found");
    }

    private void assertPasswordNotChanged() {
        verify(correspondingUser, times(0)).changePassword(newPassword, newPasswordConfirmation);
    }
}
