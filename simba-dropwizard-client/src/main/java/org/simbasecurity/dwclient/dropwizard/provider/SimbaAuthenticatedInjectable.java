package org.simbasecurity.dwclient.dropwizard.provider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.simbasecurity.dwclient.dropwizard.authenticator.SimbaAuthenticator;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentials;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsFactory;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.container.ContainerRequest;
import com.yammer.dropwizard.auth.AuthenticationException;

class SimbaAuthenticatedInjectable<P extends AuthenticatedPrincipal> extends AbstractHttpContextInjectable<P> {

	private static final Logger log = LoggerFactory.getLogger(SimbaAuthenticatedInjectable.class);

	private final SimbaAuthenticator authenticator;
	private final boolean required;
	private final SimbaCredentialsFactory simbaCredentialsFactory;
	private final DomainUserProvider<P> domainProvider;

	/**
	 * 
	 * @param authenticator The Authenticator that will compare credentials
	 * @param simbaCredentialsFactory The factory that will create SimbaCredentials from a ContainerRequest
	 * @param domainProvider The DomainProvider that will return a domain specific user/principal should it be necessary
	 * @param isRequired Will not throw an exception when no principal could be created and isRequired is false
	 */
	SimbaAuthenticatedInjectable(
			SimbaAuthenticator authenticator,
			SimbaCredentialsFactory simbaCredentialsFactory,
			DomainUserProvider<P> domainProvider,
			boolean isRequired) {
		this.authenticator = authenticator;
		this.simbaCredentialsFactory = simbaCredentialsFactory;
		this.domainProvider = domainProvider;
		this.required = isRequired;
	}

	@Override
	public P getValue(HttpContext httpContext) {
		SimbaCredentials credentials;
		try {
			final ContainerRequest containerRequest = (ContainerRequest) httpContext.getRequest();
			credentials = simbaCredentialsFactory.create(containerRequest);

			final Optional<SimbaPrincipal> result = authenticator.authenticate(credentials);
			if (result.isPresent()) {
				return domainProvider.lookUp(result.get());
			}
		} catch (AuthenticationException e) {
			log.error("Something went wrong in the authentication process", e);
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
					.entity("Something went wrong in the authentication process")
					.type(MediaType.APPLICATION_JSON)
					.build());
		}
		if (required) {
			log.warn("Error authenticating credentials: {}", credentials.getSsoToken());
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
					.entity("You are not allowed to access this resource")
					.type(MediaType.APPLICATION_JSON)
					.build());
		}
		return null;
	}
}