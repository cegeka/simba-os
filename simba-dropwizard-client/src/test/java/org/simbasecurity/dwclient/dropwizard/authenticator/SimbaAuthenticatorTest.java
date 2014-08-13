package org.simbasecurity.dwclient.dropwizard.authenticator;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.simbasecurity.dwclient.dropwizard.authenticator.SimbaAuthenticator;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentials;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaPrincipal;
import org.simbasecurity.dwclient.exception.SimbaUnavailableException;
import org.simbasecurity.dwclient.gateway.SimbaGateway;
import org.simbasecurity.dwclient.test.rule.MockitoRule;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;

public class SimbaAuthenticatorTest {

	@Rule
	public MockitoRule mockitoRule = MockitoRule.create();
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private SimbaGateway simbaGatewayMock;

	private SimbaAuthenticator authenticator;

	@Before
	public void setUp() {
		authenticator = new SimbaAuthenticator(simbaGatewayMock);
	}

	@Test
	public void authenticate_WhenAuthenticationIsSuccessful_ReturnPrincipal() throws Exception {
		SimbaCredentials credentials = mock(SimbaCredentials.class);
		Optional<SimbaPrincipal> expected = Optional.of(new SimbaPrincipal("username", "token"));
		when(simbaGatewayMock.authenticate(credentials)).thenReturn(expected);

		Optional<SimbaPrincipal> actual = authenticator.authenticate(credentials);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void authenticate_WhenConnectionToSimbaFails_ThrowAuthenticationException() throws Exception {
		SimbaCredentials credentials = mock(SimbaCredentials.class);
		when(simbaGatewayMock.authenticate(credentials)).thenThrow(new SimbaUnavailableException());

		expectedException.expect(AuthenticationException.class);

		authenticator.authenticate(credentials);
	}

}
