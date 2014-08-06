package org.simbasecurity.dwclient.dropwizard.provider;

import javax.inject.Inject;

import org.simbasecurity.dwclient.dropwizard.authenticator.SimbaAuthenticator;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentials;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsFactory;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

public class SimbaAuthenticatedProvider implements InjectableProvider<Authenticated, Parameter> {

	private final SimbaAuthenticator authenticator;
	private final SimbaCredentialsFactory simbaCredentialsFactory;
	private final DomainUserProvider<? extends AuthenticatedPrincipal> domainProvider;

	/**
	 * Creates a new {@link SimbaAuthenticatedProvider} with the given {@link com.yammer.dropwizard.auth.Authenticator} and realm.
	 * 
	 * @param authenticator the authenticator which will take the {@link SimbaCredentials} and
	 *        convert them into instances of {@code T}
	 * @param domainProvider TODO
	 * @param realm the name of the authentication realm
	 */
	@Inject
	public SimbaAuthenticatedProvider(SimbaAuthenticator authenticator, SimbaCredentialsFactory simbaCredentialsFactory,
			DomainUserProvider<? extends AuthenticatedPrincipal> domainProvider) {
		this.authenticator = authenticator;
		this.simbaCredentialsFactory = simbaCredentialsFactory;
		this.domainProvider = domainProvider;
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public Injectable<? extends AuthenticatedPrincipal> getInjectable(ComponentContext ic, Authenticated a, Parameter c) {
		return new SimbaAuthenticatedInjectable<>(authenticator, simbaCredentialsFactory, domainProvider, a.required());
	}
}