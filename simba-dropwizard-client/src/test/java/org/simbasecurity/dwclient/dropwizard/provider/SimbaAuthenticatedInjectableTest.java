package org.simbasecurity.dwclient.dropwizard.provider;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.simbasecurity.dwclient.dropwizard.authenticator.SimbaAuthenticator;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentials;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsBuilderForTests;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsFactory;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaPrincipal;
import org.simbasecurity.dwclient.test.dropwizard.matchers.ContainerRequestBuilderForTests;
import org.simbasecurity.dwclient.test.dropwizard.matchers.WebApplicationExceptionMatcher;
import org.simbasecurity.dwclient.test.rule.MockitoRule;

import com.google.common.base.Optional;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.container.ContainerRequest;
import com.yammer.dropwizard.auth.AuthenticationException;

public class SimbaAuthenticatedInjectableTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	@Rule
	public MockitoRule mockitoRule = MockitoRule.create();

	@Mock
	private SimbaAuthenticator authenticatorMock;
	@Mock
	private SimbaCredentialsFactory simbaCredentialsFactoryMock;
	@Mock
	private DomainUserProvider<AuthenticatedPrincipal> domainProviderMock;
	@Mock
	private HttpContext dummyHttpContext;
	private SimbaCredentials simbaCredentials;

	private SimbaAuthenticatedInjectable<AuthenticatedPrincipal> injectable;

	@Before
	public void setUp() {
		injectable = new SimbaAuthenticatedInjectable<AuthenticatedPrincipal>(authenticatorMock, simbaCredentialsFactoryMock, domainProviderMock, false);
		simbaCredentials = new SimbaCredentialsBuilderForTests().build();
		ContainerRequest containerRequest = new ContainerRequestBuilderForTests().build();
		when(dummyHttpContext.getRequest()).thenReturn(containerRequest);
		when(simbaCredentialsFactoryMock.create(containerRequest)).thenReturn(simbaCredentials);
	}

	@Test
	public void getValue_WhenAuthenticatorThrowsAuthenticationException_ThenThrowsWebAppException() throws Exception {
		when(authenticatorMock.authenticate(simbaCredentials)).thenThrow(new AuthenticationException(""));

		expectedException.expect(WebApplicationExceptionMatcher.webApplicationException(Response.Status.UNAUTHORIZED));

		injectable.getValue(dummyHttpContext);
	}

	@Test
	public void getValue_WhenAuthenticatorReturnsEmptyOptional_ThenDomainProviderIsNotCalled() throws Exception {
		when(authenticatorMock.authenticate(simbaCredentials)).thenReturn(Optional.<SimbaPrincipal> absent());

		injectable.getValue(dummyHttpContext);

		verifyZeroInteractions(domainProviderMock);
	}

	@Test
	public void getValue_WhenAuthenticatorReturnsEmptyOptional_AndIsRequired_ThenThrowsWebAppException() throws Exception {
		when(authenticatorMock.authenticate(simbaCredentials)).thenReturn(Optional.<SimbaPrincipal> absent());
		injectable = new SimbaAuthenticatedInjectable<AuthenticatedPrincipal>(authenticatorMock, simbaCredentialsFactoryMock, domainProviderMock, true);

		expectedException.expect(WebApplicationExceptionMatcher.webApplicationException(Response.Status.UNAUTHORIZED));

		injectable.getValue(dummyHttpContext);
	}

	@Test
	public void getValue_WhenDomainProviderReturnsNull_AndIsRequired_ThenNoExceptionIsThrown() throws Exception {
		SimbaPrincipal principal = new SimbaPrincipal("user", "token");
		when(authenticatorMock.authenticate(simbaCredentials)).thenReturn(Optional.of(principal));

		when(domainProviderMock.lookUp(principal)).thenReturn(null);

		injectable = new SimbaAuthenticatedInjectable<AuthenticatedPrincipal>(authenticatorMock, simbaCredentialsFactoryMock, domainProviderMock, true);

		AuthenticatedPrincipal actual = injectable.getValue(dummyHttpContext);

		assertThat(actual).isNull();
	}

	@Test
	public void getValue_WhenAuthenticatorReturnsPresentPrincipal_ThenDomainProviderIsCalledToLookupDomainPrincipalWithPrincipal() throws Exception {
		SimbaPrincipal principal = new SimbaPrincipal("user", "token");
		when(authenticatorMock.authenticate(simbaCredentials)).thenReturn(Optional.of(principal));

		AuthenticatedPrincipal expectedDomainPrincipal = mock(AuthenticatedPrincipal.class);
		when(domainProviderMock.lookUp(principal)).thenReturn(expectedDomainPrincipal);
		AuthenticatedPrincipal actualDomainPrincipal = injectable.getValue(dummyHttpContext);

		assertThat(actualDomainPrincipal).isEqualTo(expectedDomainPrincipal);
	}

}
