package org.simbasecurity.dwclient.gateway.resources.roles;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.dwclient.gateway.representations.AddRoleToUsersR;
import org.simbasecurity.dwclient.gateway.representations.RemoveRoleFromUserR;
import org.simbasecurity.dwclient.gateway.representations.SimbaRoleR;
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

public class SimbaRoleService extends AbstractSimbaService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	public SimbaRoleService(@Named("simbaManagerWebResource") WebResource resource) {
		super(resource);
	}

	public SimbaRoleR findRoleByName(String ssoToken, String rolename) {
		ClientResponse clientResponse = getSimbaResource()
				.path("role")
				.path("findAll")
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.cookie(new Cookie(AuthenticationConstants.SIMBA_SSO_TOKEN, ssoToken))
				.get(ClientResponse.class);

		handleError("findRole", rolename, null, clientResponse, logger);

		List<SimbaRoleR> roles = Lists.newArrayList(clientResponse.getEntity(SimbaRoleR[].class));

		Optional<SimbaRoleR> result = FluentIterable.from(roles).firstMatch(withRoleName(rolename));
		if (!result.isPresent()) {
			throw new IllegalArgumentException(String.format("No role found for name %s.", rolename));
		} else {
			return result.get();
		}
	}

	public void addRoleToUser(String ssoToken, SimbaRoleR simbaRole, SimbaUserR simbaUser) {
		checkRoleNotNull(simbaRole);
		checkUserNotNull(simbaUser);

		AddRoleToUsersR postEntity = new AddRoleToUsersR(simbaRole, simbaUser);

		ClientResponse clientResponse = getSimbaResource()
				.path("role")
				.path("addUsers")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.cookie(new Cookie(AuthenticationConstants.SIMBA_SSO_TOKEN, ssoToken))
				.post(ClientResponse.class, postEntity);

		handleError("addRole", simbaRole.getName(), simbaUser.getUserName(), clientResponse, logger);
	}

	public void removeRoleFromUser(String ssoToken, SimbaRoleR simbaRole, SimbaUserR simbaUser) {
		checkRoleNotNull(simbaRole);
		checkUserNotNull(simbaUser);

		RemoveRoleFromUserR postEntity = new RemoveRoleFromUserR(simbaRole, simbaUser);

		ClientResponse clientResponse = getSimbaResource()
				.path("role")
				.path("removeUser")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.cookie(new Cookie(AuthenticationConstants.SIMBA_SSO_TOKEN, ssoToken))
				.post(ClientResponse.class, postEntity);

		handleError("removeRole", simbaRole.getName(), simbaUser.getUserName(), clientResponse, logger);
	}

	private void checkUserNotNull(SimbaUserR simbaUser) {
		if (simbaUser == null) {
			throw new IllegalArgumentException("User cannot be null.");
		}
	}

	private void checkRoleNotNull(SimbaRoleR simbaRole) {
		if (simbaRole == null) {
			throw new IllegalArgumentException("Role cannot be null.");
		}
	}

	private Predicate<SimbaRoleR> withRoleName(final String rolename) {
		return new Predicate<SimbaRoleR>() {
			@Override
			public boolean apply(SimbaRoleR role) {
				return rolename.equals(role.getName());
			}
		};
	}

}
