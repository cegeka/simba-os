package org.simbasecurity.core.service.manager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.SessionRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.service.ErrorSender;
import org.simbasecurity.core.service.manager.dto.ChangePasswordDTO;

@RunWith(MockitoJUnitRunner.class)
public class UserManagerServiceTest {

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserManagerService service;

	private ChangePasswordDTO changePasswordDTO;

	private User correspondingUser;

	private SSOToken ssoToken = new SSOToken("56161616");
	private String userName = "user1";
	private String newPassword = "newBlaBla1";
	private String newPasswordConfirmation = "newBlaBla1";

	private HttpServletResponse responseMock;

	@Before
	public void setup() {

		changePasswordDTO = new ChangePasswordDTO();
		changePasswordDTO.setUserName(userName);
		changePasswordDTO.setNewPassword(newPassword);
		changePasswordDTO.setNewPasswordConfirmation(newPasswordConfirmation);

		Session aSession = mock(Session.class);
		when(sessionRepository.findBySSOToken(ssoToken)).thenReturn(aSession);

		correspondingUser = mock(User.class);
		when(aSession.getUser()).thenReturn(correspondingUser);
		when(userRepository.findByName(userName)).thenReturn(correspondingUser);
		when(correspondingUser.getUserName()).thenReturn(userName);

		responseMock = mock(HttpServletResponse.class);
	}

	@Test
	public void changePasswordOk_headerToken() {

		service.changeUserPassword(ssoToken.getToken(), null, changePasswordDTO, responseMock);

		verify(correspondingUser).changePassword(newPassword, newPasswordConfirmation);
		verifyZeroInteractions(responseMock);
	}

	@Test
	public void changePasswordOk_cookieToken() {

		service.changeUserPassword(null, ssoToken.getToken(), changePasswordDTO, responseMock);

		verify(correspondingUser).changePassword(newPassword, newPasswordConfirmation);
		verifyZeroInteractions(responseMock);
	}

	@Test
	public void changePasswordNotOk_noSsoToken_unauthorized() throws IOException {

		service.changeUserPassword(null, null, changePasswordDTO, responseMock);

		assertPasswordNotChanged();
		assertUnauthorizedError();
	}

	@Test
	public void changePasswordNotOk_noActiveSession_unauthorized() throws IOException {

		when(sessionRepository.findBySSOToken(ssoToken)).thenReturn(null);
		service.changeUserPassword(ssoToken.getToken(), null, changePasswordDTO, responseMock);

		assertPasswordNotChanged();
		assertUnauthorizedError();
	}

	@Test
	public void changePasswordNotOk_userNotFound() throws IOException {

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
