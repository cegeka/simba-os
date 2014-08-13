package org.simbasecurity.dwclient.gateway;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.simbasecurity.dwclient.dropwizard.config.SimbaManagerRestConfiguration;
import org.simbasecurity.dwclient.exception.SimbaUnavailableException;
import org.simbasecurity.dwclient.gateway.SimbaGateway;
import org.simbasecurity.dwclient.gateway.SimbaManagerRestGateway;
import org.simbasecurity.dwclient.gateway.representations.SimbaRoleR;
import org.simbasecurity.dwclient.gateway.representations.SimbaUserR;
import org.simbasecurity.dwclient.gateway.resources.roles.SimbaRoleService;
import org.simbasecurity.dwclient.gateway.resources.users.SimbaUserService;
import org.simbasecurity.dwclient.test.rule.MockitoRule;
import org.slf4j.Logger;

import com.google.common.base.Optional;

public class SimbaManagerRestGatewayTest {

	private static final String APP_PASSWORD = "appUserPw";
	private static final String APP_USER = "appUser";
	private static final String APP_USER_ROLE = "simbaManagerRole";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public MockitoRule mockitoRule = MockitoRule.create();

	@Mock
	private SimbaGateway simbaGatewayMock;
	@Mock
	private SimbaRoleService simbaRoleServiceMock;
	@Mock
	private SimbaUserService simbaUserServiceMock;

	private SimbaManagerRestGateway simbaManagerRestGateway;

	@Before
	public void setUp() {
		SimbaManagerRestConfiguration simbaConfig = new SimbaManagerRestConfiguration();
		simbaConfig.setAppPassword(APP_PASSWORD);
		simbaConfig.setAppUser(APP_USER);
		simbaConfig.setAppUserRole(APP_USER_ROLE);
		simbaManagerRestGateway = new SimbaManagerRestGateway(simbaConfig, simbaGatewayMock, simbaRoleServiceMock, simbaUserServiceMock);
	}

	@Test
	public void loginWithAppUser_WhenNoSSOToken_ThrowsIllegalStateException() throws Exception {
		String username = "herp";
		String rolename = "derp";

		when(simbaGatewayMock.login(APP_USER, APP_PASSWORD)).thenReturn(Optional.<String> absent());

		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage(String.format("Could not log in to Simba with app user %s.", APP_USER));

		simbaManagerRestGateway.assignRoleToUser(rolename, username);
	}

	@Test
	public void loginWithAppUser_WhenNoSSOToken_LogsUnauthorized() throws Exception {
		Logger loggerMock = mock(Logger.class);
		Whitebox.setInternalState(simbaManagerRestGateway, "logger", loggerMock);
		when(simbaGatewayMock.login(APP_USER, APP_PASSWORD)).thenReturn(Optional.<String> absent());

		try {
			simbaManagerRestGateway.assignRoleToUser("derp", "herp");
		} catch (Exception e) {
		}

		verify(loggerMock, times(1)).error("Could not log in to Simba with app user {}.", APP_USER);
	}

	@Test
	public void assignRoleToUser_WhenRolenameIsNull_ThrowsIllegalArgumentException() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("role cannot be null");

		simbaManagerRestGateway.assignRoleToUser(null, "username");
	}

	@Test
	public void assignRoleToUser_WhenUsernameIsNull_ThrowsIllegalArgumentException() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("username cannot be null");

		simbaManagerRestGateway.assignRoleToUser("role", null);
	}

	@Test
	public void assignRoleToUser_WhenUsernameDoesNotExist_ThrowsIllegalArgumentException() throws Exception {
		String username = "ivar";
		String rolename = "role name";
		logInWithValidUser();

		when(simbaUserServiceMock.findUserByName(anyString(), eq(username))).thenThrow(new IllegalArgumentException());
		when(simbaRoleServiceMock.findRoleByName(anyString(), eq(rolename))).thenReturn(new SimbaRoleR());

		expectedException.expect(IllegalArgumentException.class);

		simbaManagerRestGateway.assignRoleToUser(rolename, username);
	}

	@Test
	public void assignRoleToUser_WhenRolenameDoesNotExist_ThrowsIllegalArgumentException() throws Exception {
		String username = "ivar";
		String rolename = "role name";
		logInWithValidUser();
		when(simbaUserServiceMock.findUserByName(anyString(), eq(username))).thenReturn(new SimbaUserR(username));
		when(simbaRoleServiceMock.findRoleByName(anyString(), eq(rolename))).thenThrow(new IllegalArgumentException());

		expectedException.expect(IllegalArgumentException.class);

		simbaManagerRestGateway.assignRoleToUser(rolename, username);
	}

	@Test
	public void assignRoleToUser_AssignsLookedUpSimbaRoleToLookedUpSimbaUser() throws Exception {
		String username = "herp";
		String rolename = "derp";

		String ssoToken = logInWithValidUser();
		SimbaRoleR simbaRole = new SimbaRoleR();
		SimbaUserR simbaUser = new SimbaUserR("simbauser");
		when(simbaRoleServiceMock.findRoleByName(ssoToken, rolename)).thenReturn(simbaRole);
		when(simbaUserServiceMock.findUserByName(ssoToken, username)).thenReturn(simbaUser);

		simbaManagerRestGateway.assignRoleToUser(rolename, username);

		verify(simbaGatewayMock, times(1)).login(APP_USER, APP_PASSWORD);
		verify(simbaRoleServiceMock, times(1)).findRoleByName(ssoToken, rolename);
		verify(simbaUserServiceMock, times(1)).findUserByName(ssoToken, username);
		verify(simbaRoleServiceMock, times(1)).addRoleToUser(ssoToken, simbaRole, simbaUser);
	}

	@Test
	public void unassignRoleFromUser_WhenRolenameIsNull_ThrowsIllegalArgumentException() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("role cannot be null");

		simbaManagerRestGateway.unassignRoleFromUser(null, "username");
	}

	@Test
	public void unassignRoleFromUser_WhenUsernameIsNull_ThrowsIllegalArgumentException() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("username cannot be null");

		simbaManagerRestGateway.unassignRoleFromUser("role", null);
	}

	@Test
	public void unassignRoleFromUser_WhenUsernameDoesNotExist_ThrowsIllegalArgumentException() throws Exception {
		String username = "ivar";
		String rolename = "role name";
		logInWithValidUser();

		when(simbaUserServiceMock.findUserByName(anyString(), eq(username))).thenThrow(new IllegalArgumentException());
		when(simbaRoleServiceMock.findRoleByName(anyString(), eq(rolename))).thenReturn(new SimbaRoleR());

		expectedException.expect(IllegalArgumentException.class);

		simbaManagerRestGateway.unassignRoleFromUser(rolename, username);
	}

	@Test
	public void unassignRoleFromUser_WhenRolenameDoesNotExist_ThrowsIllegalArgumentException() throws Exception {
		String username = "ivar";
		String rolename = "role name";
		logInWithValidUser();
		when(simbaUserServiceMock.findUserByName(anyString(), eq(username))).thenReturn(new SimbaUserR(username));
		when(simbaRoleServiceMock.findRoleByName(anyString(), eq(rolename))).thenThrow(new IllegalArgumentException());

		expectedException.expect(IllegalArgumentException.class);

		simbaManagerRestGateway.unassignRoleFromUser(rolename, username);
	}

	@Test
	public void unassignRoleFromUser_RemovesLookedUpSimbaRoleFromLookedUpSimbaUser() throws Exception {
		String username = "herp";
		String rolename = "derp";

		String ssoToken = logInWithValidUser();
		SimbaRoleR simbaRole = new SimbaRoleR();
		SimbaUserR simbaUser = new SimbaUserR("simbauser");
		when(simbaRoleServiceMock.findRoleByName(ssoToken, rolename)).thenReturn(simbaRole);
		when(simbaUserServiceMock.findUserByName(ssoToken, username)).thenReturn(simbaUser);

		simbaManagerRestGateway.unassignRoleFromUser(rolename, username);

		verify(simbaGatewayMock, times(1)).login(APP_USER, APP_PASSWORD);
		verify(simbaRoleServiceMock, times(1)).findRoleByName(ssoToken, rolename);
		verify(simbaUserServiceMock, times(1)).findUserByName(ssoToken, username);
		verify(simbaRoleServiceMock, times(1)).removeRoleFromUser(ssoToken, simbaRole, simbaUser);
	}

	@Test
	public void isSimbaRestManagerAlive_WhenRoleServiceCallWasSuccessfulAndRoleExists_ThenSimbaManagerIsAlive() throws Exception {
		String ssoToken = logInWithValidUser();
		when(simbaRoleServiceMock.findRoleByName(ssoToken, APP_USER_ROLE)).thenReturn(new SimbaRoleR());

		boolean actual = simbaManagerRestGateway.isSimbaRestManagerAlive();

		assertThat(actual).isTrue();
	}

	@Test
	public void isSimbaRestManagerAlive_WhenRoleServiceCallWasSuccessfulAndRoleDoesNotExist_ThenSimbaManagerIsAlive() throws Exception {
		String ssoToken = logInWithValidUser();
		when(simbaRoleServiceMock.findRoleByName(ssoToken, APP_USER_ROLE)).thenReturn(new SimbaRoleR());

		boolean actual = simbaManagerRestGateway.isSimbaRestManagerAlive();

		assertThat(actual).isTrue();
	}

	@Test
	public void isSimbaRestManagerAlive_WhenRoleServiceCallReturnsErrorStatus_ThenSimbaManagerIsNotAlive() throws Exception {
		String ssoToken = logInWithValidUser();
		when(simbaRoleServiceMock.findRoleByName(ssoToken, APP_USER_ROLE)).thenThrow(new IllegalArgumentException());

		boolean actual = simbaManagerRestGateway.isSimbaRestManagerAlive();

		assertThat(actual).isFalse();
	}

	private String logInWithValidUser() throws SimbaUnavailableException {
		String ssoToken = "validSSOToken";
		when(simbaGatewayMock.login(APP_USER, APP_PASSWORD)).thenReturn(Optional.of(ssoToken));
		return ssoToken;
	}
}
