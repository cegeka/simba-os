package org.simbasecurity.dwclient.gateway.resources.users;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.dwclient.gateway.representations.SimbaUserR;
import org.simbasecurity.dwclient.gateway.resources.AbstractSimbaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SimbaUserService extends AbstractSimbaService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	public SimbaUserService(@Named("simbaManagerWebResource") WebResource resource) {
		super(resource);
	}

	public SimbaUserR findUserByName(String ssoToken, String username) {
		ClientResponse clientResponse = getSimbaResource()
				.path("user")
				.path("findAll")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.cookie(new Cookie(AuthenticationConstants.SIMBA_SSO_TOKEN, ssoToken))
				.get(ClientResponse.class);

		handleError("findUser", username, clientResponse, logger);

		List<SimbaUserR> users = Lists.newArrayList(clientResponse.getEntity(SimbaUserR[].class));

		Optional<SimbaUserR> result = FluentIterable.from(users).firstMatch(withName(username));
		if (!result.isPresent()) {
			throw new IllegalArgumentException(String.format("No user found for name %s.", username));
		} else {
			return result.get();
		}
	}

	private Predicate<SimbaUserR> withName(final String username) {
		return new Predicate<SimbaUserR>() {
			@Override
			public boolean apply(SimbaUserR user) {
				return username.equals(user.getUserName());
			}
		};
	}
}
