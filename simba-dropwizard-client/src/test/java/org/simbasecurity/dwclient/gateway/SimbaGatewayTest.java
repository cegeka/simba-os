package org.simbasecurity.dwclient.gateway;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.simbasecurity.dwclient.gateway.SimbaGateway.*;

import java.net.SocketException;

import org.apache.thrift.TException;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService.Client;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentials;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsBuilderForTests;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsFactory;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaPrincipal;
import org.simbasecurity.dwclient.exception.SimbaUnavailableException;
import org.simbasecurity.dwclient.test.rule.MockitoRule;
import org.simbasecurity.dwclient.test.stub.simba.ActionDescriptorBuilderForTests;

import com.google.common.base.Optional;
import com.sun.jersey.spi.container.ContainerRequest;

public class SimbaGatewayTest {

	@Rule
	public MockitoRule mockitoRule = MockitoRule.create();
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private static final String SIMBA_WEB_URL = "http://simba.wayneindustries.com/simba";

	@Mock
	private SimbaServiceFactory simbaServiceFactoryMock;
	@Mock
	private SimbaCredentialsFactory simbaCredentialsFactoryMock;

	private SimbaGateway simbaGateway;

	@Before
	public void setUp() {
		simbaGateway = new SimbaGateway(SIMBA_WEB_URL, simbaServiceFactoryMock, simbaCredentialsFactoryMock);
	}

	@Test
	public void createAuthenticationService_WhenConnectionWithSimbaFails_ThrowsSimbaUnavailableException() throws Exception {
		when(simbaServiceFactoryMock.createTHttpClient(SIMBA_WEB_URL + "/" + SIMBA_AUTHENTICATION_SERVICE)).thenThrow(new TTransportException());

		expectedException.expect(SimbaUnavailableException.class);
		simbaGateway.createAuthenticationService();
	}

	@Test
	public void createAuthenticationService_WhenConnectionToSimbaEstablished_ReturnsClient() throws Exception {
		Client expectedClient = setupSimbaServiceToReturnASimbaAuthenticationService();

		Client actual = simbaGateway.createAuthenticationService();
		assertThat(actual).isEqualTo(expectedClient);
	}

	@Test
	public void createAuthenticationService_WhenTHttpClientIsNotOpen_ReturnsClientByEstablishingANewConnection() throws Exception {
		Client originalClient = mock(Client.class);
		THttpClient tHttpClientMock = mock(THttpClient.class);
		Whitebox.setInternalState(simbaGateway, "tHttpClient", tHttpClientMock);
		Whitebox.setInternalState(simbaGateway, "authenticationFilterService", originalClient);

		THttpClient newlyCreatedTHttpClient = new THttpClient(SIMBA_WEB_URL);
		when(simbaServiceFactoryMock.createTHttpClient(SIMBA_WEB_URL + "/" + SIMBA_AUTHENTICATION_SERVICE)).thenReturn(newlyCreatedTHttpClient);
		Client expectedClient = new Client(null);
		when(simbaServiceFactoryMock.createJSONAuthenticationFilterService(newlyCreatedTHttpClient)).thenReturn(expectedClient);

		when(tHttpClientMock.isOpen()).thenReturn(false);

		Client actual = simbaGateway.createAuthenticationService();

		assertThat(actual).isEqualTo(expectedClient);

		verify(simbaServiceFactoryMock, times(1)).createTHttpClient(SIMBA_WEB_URL + "/" + SIMBA_AUTHENTICATION_SERVICE);
		verify(simbaServiceFactoryMock, times(1)).createJSONAuthenticationFilterService(any(THttpClient.class));
	}

	@Test
	public void authenticate_WhenAuthenticationCallWentWrong_ThrowsSimbaUnavailableException() throws Exception {
		SimbaCredentials credentials = mock(SimbaCredentials.class);
		RequestData requestData = mock(RequestData.class);
		when(credentials.asRequestData()).thenReturn(requestData);

		Client authenticationServiceMock = setupSimbaServiceToReturnASimbaAuthenticationService();
		when(authenticationServiceMock.processRequest(requestData, SESSION_AUTHENTICATE_CHAIN)).thenThrow(new TException());

		expectedException.expect(SimbaUnavailableException.class);
		simbaGateway.authenticate(credentials);
	}

	@Test
	public void authenticate_WhenPrincipalWasSet_ReturnPrincipal() throws Exception {
		SimbaCredentials credentials = mock(SimbaCredentials.class);
		RequestData requestData = mock(RequestData.class);
		when(credentials.asRequestData()).thenReturn(requestData);

		Client authenticationServiceMock = setupSimbaServiceToReturnASimbaAuthenticationService();
		String principal = "simbaUsername";
		String token = "token";
		ActionDescriptor actionDescriptor = new ActionDescriptorBuilderForTests()
				.withActionTypes(ActionType.DO_FILTER_AND_SET_PRINCIPAL)
				.withPrincipal(principal)
				.withSsoToken(new SSOToken(token))
				.build();
		when(authenticationServiceMock.processRequest(requestData, SESSION_AUTHENTICATE_CHAIN)).thenReturn(actionDescriptor);

		Optional<SimbaPrincipal> simbaPrincipal = simbaGateway.authenticate(credentials);
		assertThat(simbaPrincipal.get()).isEqualTo(new SimbaPrincipal(principal, token));
	}

	@Test
	public void authenticate_WhenPrincipalWasNotSet_ReturnsAbsentPrincipal() throws Exception {
		SimbaCredentials credentials = mock(SimbaCredentials.class);
		RequestData requestData = mock(RequestData.class);
		when(credentials.asRequestData()).thenReturn(requestData);

		Client authenticationServiceMock = setupSimbaServiceToReturnASimbaAuthenticationService();
		String principal = "simbaUsername";
		String ssoToken = "ssotoken";
		ActionDescriptor actionDescriptor = new ActionDescriptorBuilderForTests()
				.withActionTypes(ActionType.ADD_PARAMETER_TO_TARGET)
				.withPrincipal(principal)
				.withSsoToken(new SSOToken(ssoToken))
				.build();
		when(authenticationServiceMock.processRequest(requestData, SimbaGateway.SESSION_AUTHENTICATE_CHAIN)).thenReturn(actionDescriptor);

		Optional<SimbaPrincipal> simbaPrincipal = simbaGateway.authenticate(credentials);
		assertThat(simbaPrincipal.isPresent()).isFalse();
	}

	@Test
	public void authenticate_WhenClientIsABrowser_ExecuteBrowserLoginChain() throws Exception {
		SimbaCredentials credentials = mock(SimbaCredentials.class);
		when(credentials.isClientABrowser()).thenReturn(true);

		Client authenticationServiceMock = setupSimbaServiceToReturnASimbaAuthenticationService();

		simbaGateway.authenticate(credentials);
		verify(authenticationServiceMock).processRequest(any(RequestData.class), eq(SimbaGateway.SESSION_AUTHENTICATE_CHAIN));
	}

	@Test
	public void authenticate_WhenClientIsNotABrowser_ExecuteWsLoginChain() throws Exception {
		SimbaCredentials credentials = mock(SimbaCredentials.class);
		when(credentials.isClientABrowser()).thenReturn(false);

		Client authenticationServiceMock = setupSimbaServiceToReturnASimbaAuthenticationService();

		simbaGateway.authenticate(credentials);
		verify(authenticationServiceMock).processRequest(any(RequestData.class), eq(SimbaGateway.SESSION_AUTHENTICATE_CHAIN));
	}

	@Test
	public void authenticate_THttpClientAlwaysGetsClosed() throws Exception {
		Client expectedClient = mock(Client.class);
		THttpClient tHttpClientMock = mock(THttpClient.class);
		when(simbaServiceFactoryMock.createTHttpClient(SIMBA_WEB_URL + "/" + SIMBA_AUTHENTICATION_SERVICE)).thenReturn(tHttpClientMock);
		when(simbaServiceFactoryMock.createJSONAuthenticationFilterService(tHttpClientMock)).thenReturn(expectedClient);
		Client authenticationServiceMock = expectedClient;

		when(authenticationServiceMock.processRequest(any(RequestData.class), eq(SimbaGateway.SESSION_AUTHENTICATE_CHAIN)))
				.thenReturn(new ActionDescriptorBuilderForTests().withActionTypes().build());

		simbaGateway.authenticate(new SimbaCredentialsBuilderForTests().build());

		verify(tHttpClientMock, times(1)).close();
	}

	@Test
	public void authenticate_THttpClientEvenGetsClosedAfterAnExceptionOccurred() throws Exception {
		Client expectedClient = mock(Client.class);
		THttpClient tHttpClientMock = mock(THttpClient.class);
		when(simbaServiceFactoryMock.createTHttpClient(SIMBA_WEB_URL + SIMBA_AUTHENTICATION_SERVICE)).thenReturn(tHttpClientMock);
		when(simbaServiceFactoryMock.createJSONAuthenticationFilterService(tHttpClientMock)).thenReturn(expectedClient);
		Client authenticationServiceMock = expectedClient;

		when(authenticationServiceMock.processRequest(any(RequestData.class), any(String.class))).thenThrow(new TException());

		expectedException.expect(SimbaUnavailableException.class);
		simbaGateway.authenticate(new SimbaCredentialsBuilderForTests().build());

		verify(tHttpClientMock, times(1)).close();
	}

	@Test
	public void login() throws Exception {
		Client authenticationServicemock = setupSimbaServiceToReturnASimbaAuthenticationService();
		ContainerRequest containerRequestMock = mock(ContainerRequest.class);
		SimbaCredentials simbaCredentials = mock(SimbaCredentials.class);
		RequestData requestData = mock(RequestData.class);
		SSOToken expectedSSOToken = new SSOToken("token");
		ActionDescriptor actionDescriptor = new ActionDescriptorBuilderForTests()
				.withActionTypes(ActionType.MAKE_COOKIE)
				.withSsoToken(expectedSSOToken)
				.build();

		when(simbaCredentialsFactoryMock.create(containerRequestMock, false)).thenReturn(simbaCredentials);
		when(simbaCredentials.asRequestData()).thenReturn(requestData);
		when(authenticationServicemock.processRequest(requestData, LOGIN_AUTHENTICATE_CHAIN)).thenReturn(actionDescriptor);

		Optional<String> actualSSOToken = simbaGateway.login(containerRequestMock);
		assertThat(actualSSOToken.isPresent()).isTrue();
		assertThat(actualSSOToken.get()).isEqualTo(expectedSSOToken.getToken());
	}

	@Test
	public void login_WhenActionDescriptorActionTypesIsNull_ThenReturnsEmptySSOToken() throws Exception {
		Client authenticationServicemock = setupSimbaServiceToReturnASimbaAuthenticationService();
		ContainerRequest containerRequestMock = mock(ContainerRequest.class);
		SimbaCredentials simbaCredentials = mock(SimbaCredentials.class);
		RequestData requestData = mock(RequestData.class);
		ActionDescriptor actionDescriptor = new ActionDescriptorBuilderForTests().build();

		when(simbaCredentialsFactoryMock.create(containerRequestMock, false)).thenReturn(simbaCredentials);
		when(simbaCredentials.asRequestData()).thenReturn(requestData);
		when(authenticationServicemock.processRequest(requestData, LOGIN_AUTHENTICATE_CHAIN)).thenReturn(actionDescriptor);

		Optional<String> ssoToken = simbaGateway.login(containerRequestMock);
		assertThat(ssoToken.isPresent()).isFalse();
	}

	@Test
	public void login_WhenActionDescriptorActionTypesIsEmpty_ThenReturnsEmptySSOToken() throws Exception {
		Client authenticationServicemock = setupSimbaServiceToReturnASimbaAuthenticationService();
		ContainerRequest containerRequestMock = mock(ContainerRequest.class);
		SimbaCredentials simbaCredentials = mock(SimbaCredentials.class);
		RequestData requestData = mock(RequestData.class);
		ActionDescriptor actionDescriptor = new ActionDescriptorBuilderForTests().withActionTypes().build();

		when(simbaCredentialsFactoryMock.create(containerRequestMock, false)).thenReturn(simbaCredentials);
		when(simbaCredentials.asRequestData()).thenReturn(requestData);
		when(authenticationServicemock.processRequest(requestData, LOGIN_AUTHENTICATE_CHAIN)).thenReturn(actionDescriptor);

		Optional<String> ssoToken = simbaGateway.login(containerRequestMock);
		assertThat(ssoToken.isPresent()).isFalse();
	}

	@Test
	public void login_WhenActionDescriptorMissesMAKECOOKIE_ReturnsEmptySSOToken() throws Exception {
		Client authenticationServicemock = setupSimbaServiceToReturnASimbaAuthenticationService();
		ContainerRequest containerRequestMock = mock(ContainerRequest.class);
		SimbaCredentials simbaCredentials = mock(SimbaCredentials.class);
		RequestData requestData = mock(RequestData.class);
		ActionDescriptor actionDescriptor = new ActionDescriptorBuilderForTests().withActionTypes(ActionType.REDIRECT).build();

		when(simbaCredentialsFactoryMock.create(containerRequestMock, false)).thenReturn(simbaCredentials);
		when(simbaCredentials.asRequestData()).thenReturn(requestData);
		when(authenticationServicemock.processRequest(requestData, LOGIN_AUTHENTICATE_CHAIN)).thenReturn(actionDescriptor);

		Optional<String> ssoToken = simbaGateway.login(containerRequestMock);
		assertThat(ssoToken.isPresent()).isFalse();
	}

	@Test
	public void login_WithUsernameAndPassword() throws Exception {
		String username = "appUser";
		String password = "appPassword";
		Client authenticationServicemock = setupSimbaServiceToReturnASimbaAuthenticationService();
		SimbaCredentials simbaCredentials = mock(SimbaCredentials.class);
		RequestData requestData = mock(RequestData.class);
		SSOToken ssoToken = new SSOToken("token");
		ActionDescriptor actionDescriptor = new ActionDescriptorBuilderForTests()
				.withActionTypes(ActionType.MAKE_COOKIE)
				.withSsoToken(ssoToken)
				.build();

		when(simbaCredentialsFactoryMock.createForLogin(username, password)).thenReturn(simbaCredentials);
		when(simbaCredentials.asRequestData()).thenReturn(requestData);
		when(authenticationServicemock.processRequest(requestData, LOGIN_AUTHENTICATE_CHAIN)).thenReturn(actionDescriptor);

		Optional<String> actual = simbaGateway.login(username, password);
		assertThat(actual.get()).isEqualTo(ssoToken.getToken());
	}

	@Test
	public void logout() throws Exception {
		Client authenticationServicemock = setupSimbaServiceToReturnASimbaAuthenticationService();
		ContainerRequest containerRequestMock = mock(ContainerRequest.class);
		SimbaCredentials simbaCredentials = mock(SimbaCredentials.class);
		RequestData requestData = mock(RequestData.class);

		when(simbaCredentialsFactoryMock.create(containerRequestMock, true)).thenReturn(simbaCredentials);
		when(simbaCredentials.asRequestData()).thenReturn(requestData);

		simbaGateway.logout(containerRequestMock);

		verify(authenticationServicemock).processRequest(requestData, LOGOUT_CHAIN);
	}

	@Test
	public void isSimbaAlive_WhenTHttpFlushThrewSocketException_ReturnFalse() throws Exception {
		when(simbaServiceFactoryMock.createTHttpClient(SIMBA_WEB_URL + "/" + SIMBA_AUTHENTICATION_SERVICE)).thenThrow(
				new TTransportException(new SocketException()));

		assertThat(simbaGateway.isSimbaAlive()).isFalse();
	}

	@Test
	public void isSimbaAlive_WhenTHttpCouldFlushWithoutSocketException_ReturnTrue() throws Exception {
		when(simbaServiceFactoryMock.createTHttpClient(SIMBA_WEB_URL + "/" + SIMBA_AUTHENTICATION_SERVICE)).thenThrow(
				new TTransportException("HTTP Response code: 404"));

		assertThat(simbaGateway.isSimbaAlive()).isTrue();
	}

	private Client setupSimbaServiceToReturnASimbaAuthenticationService() throws TTransportException {
		Client expectedClient = mock(Client.class);
		THttpClient tHttpClientMock = mock(THttpClient.class);
		when(simbaServiceFactoryMock.createTHttpClient(SIMBA_WEB_URL + "/" + SIMBA_AUTHENTICATION_SERVICE)).thenReturn(tHttpClientMock);
		when(simbaServiceFactoryMock.createJSONAuthenticationFilterService(tHttpClientMock)).thenReturn(expectedClient);
		return expectedClient;
	}
}
