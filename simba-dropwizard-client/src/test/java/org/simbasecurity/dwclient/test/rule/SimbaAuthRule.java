package org.simbasecurity.dwclient.test.rule;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.simbasecurity.dwclient.dropwizard.authenticator.SimbaAuthenticator;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentials;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsFactory;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaPrincipal;
import org.simbasecurity.dwclient.dropwizard.provider.AuthenticatedPrincipal;
import org.simbasecurity.dwclient.dropwizard.provider.DomainUserProvider;
import org.simbasecurity.dwclient.dropwizard.provider.SimbaAuthenticatedProvider;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsBuilderForTests;

import com.google.common.base.Optional;
import com.sun.jersey.spi.container.ContainerRequest;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.testing.ResourceTest;

public class SimbaAuthRule implements MethodRule {

	public static final String USER_UNIQUE_EMAIL_ADRESS = "user.unique@email.com";
	public static final String USER_NON_UNIQUE_EMAIL_ADRESS = "user.non.unique@email.com";
	public static final String USER_PASSWORD = "password";
	public static final String SIMBA_ID = "SIMBA ID";

	private DomainUserProvider<? extends AuthenticatedPrincipal> domainProvider;

	public static SimbaAuthRule create(DomainUserProvider<? extends AuthenticatedPrincipal> domainProvider) {
		return new SimbaAuthRule(domainProvider);
	}

	public static SimbaAuthRule create() {
		return new SimbaAuthRule(new DomainUserProvider<AuthenticatedPrincipal>() {
			@Override
			public AuthenticatedPrincipal lookUp(SimbaPrincipal principal) {
				return null;
			}
		});
	}

	private SimbaAuthRule(DomainUserProvider<? extends AuthenticatedPrincipal> domainProvider) {
		this.domainProvider = domainProvider;
	}

	@Override
	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		if (!ResourceTest.class.isAssignableFrom(target.getClass())) {
			throw new IllegalArgumentException("This rule can only be used on tests that extend ResourceTest.");
		}
		final ResourceTest currentTest = (ResourceTest) target;
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				before(currentTest);
				try {
					base.evaluate();
				} finally {
				}
			}
		};
	}

	protected void before(ResourceTest currentTest) throws AuthenticationException {
		SimbaAuthenticator authenticatorMock = mock(SimbaAuthenticator.class);
		SimbaCredentialsFactory simbaCredentialsFactoryMock = mock(SimbaCredentialsFactory.class);

		SimbaAuthenticatedProvider simbaAuthenticatedProvider = new SimbaAuthenticatedProvider(authenticatorMock,
				simbaCredentialsFactoryMock, domainProvider);
		SimbaCredentials simbaCredentials = new SimbaCredentialsBuilderForTests().build();

		when(simbaCredentialsFactoryMock.create(any(ContainerRequest.class))).thenReturn(simbaCredentials);
		when(authenticatorMock.authenticate(simbaCredentials)).thenReturn(Optional.fromNullable(new SimbaPrincipal("user", "ssoToken")));

		currentTest.addProvider(simbaAuthenticatedProvider);
	}
}
