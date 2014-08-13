package org.simbasecurity.dwclient.dropwizard.authenticator;

import javax.inject.Inject;

import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentials;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaPrincipal;
import org.simbasecurity.dwclient.exception.SimbaUnavailableException;
import org.simbasecurity.dwclient.gateway.SimbaGateway;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;

/**
 * Verifies the provided credentials are valid by calling our Simba authentication chain
 */
public class SimbaAuthenticator implements Authenticator<SimbaCredentials, SimbaPrincipal> {

	private SimbaGateway simbaGateway;

	@Inject
	public SimbaAuthenticator(SimbaGateway simbaGateway) {
		this.simbaGateway = simbaGateway;
	}

	@Override
	public Optional<SimbaPrincipal> authenticate(SimbaCredentials credentials) throws AuthenticationException {
		try {
			return simbaGateway.authenticate(credentials);
		} catch (SimbaUnavailableException e) {
			throw new AuthenticationException(e);
		}
	}

}