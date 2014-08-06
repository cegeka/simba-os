package org.simbasecurity.dwclient.gateway;

import javax.inject.Inject;

import org.simbasecurity.dwclient.dropwizard.config.SimbaManagerRestConfiguration;
import org.simbasecurity.dwclient.exception.SimbaUnavailableException;
import org.simbasecurity.dwclient.gateway.representations.SimbaRoleR;
import org.simbasecurity.dwclient.gateway.representations.SimbaUserR;
import org.simbasecurity.dwclient.gateway.resources.roles.SimbaRoleService;
import org.simbasecurity.dwclient.gateway.resources.users.SimbaUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

public class SimbaManagerRestGateway {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private SimbaManagerRestConfiguration simbaRestConfig;
	private SimbaGateway simbaGateway;
	private SimbaRoleService simbaRoleService;
	private SimbaUserService simbaUserService;

	@Inject
	public SimbaManagerRestGateway(SimbaManagerRestConfiguration simbaConfig, SimbaGateway simbaGateway, SimbaRoleService simbaRoleService,
			SimbaUserService simbaUserService) {
		this.simbaRestConfig = simbaConfig;
		this.simbaGateway = simbaGateway;
		this.simbaRoleService = simbaRoleService;
		this.simbaUserService = simbaUserService;
	}

	public boolean isSimbaRestManagerAlive() throws SimbaUnavailableException {
		SimbaRoleR roleThatShouldExist;
		try {
			roleThatShouldExist = simbaRoleService.findRoleByName(loginWithAppUser(), getAppUserRole());
		} catch (IllegalArgumentException e) {
			return false;
		}
		return roleThatShouldExist != null;
	}

	public void assignRoleToUser(String rolename, String username) throws SimbaUnavailableException {
		checkUsernameNotNull(username);
		checkRolenameNotNull(rolename);

		String appUserSSOToken = loginWithAppUser();

		SimbaRoleR simbaRole = simbaRoleService.findRoleByName(appUserSSOToken, rolename);
		SimbaUserR simbaUser = simbaUserService.findUserByName(appUserSSOToken, username);
		simbaRoleService.addRoleToUser(appUserSSOToken, simbaRole, simbaUser);
	}

	public void unassignRoleFromUser(String rolename, String username) throws SimbaUnavailableException {
		checkUsernameNotNull(username);
		checkRolenameNotNull(rolename);

		String appUserSSOToken = loginWithAppUser();

		SimbaRoleR simbaRole = simbaRoleService.findRoleByName(appUserSSOToken, rolename);
		SimbaUserR simbaUser = simbaUserService.findUserByName(appUserSSOToken, username);
		simbaRoleService.removeRoleFromUser(appUserSSOToken, simbaRole, simbaUser);
	}

	private void checkRolenameNotNull(String rolename) {
		if (Strings.isNullOrEmpty(rolename)) {
			throw new IllegalArgumentException("role cannot be null");
		}
	}

	private void checkUsernameNotNull(String username) {
		if (Strings.isNullOrEmpty(username)) {
			throw new IllegalArgumentException("username cannot be null");
		}
	}

	private String loginWithAppUser() throws SimbaUnavailableException {
		Optional<String> ssoToken = simbaGateway.login(getAppUser(), getAppPassword());
		if (!ssoToken.isPresent()) {
			logger.error("Could not log in to Simba with app user {}.", getAppUser());
			throw new IllegalStateException(String.format("Could not log in to Simba with app user %s.", getAppUser()));
		}
		return ssoToken.get();
	}

	private String getAppUser() {
		return simbaRestConfig.getAppUser();
	}

	private String getAppPassword() {
		return simbaRestConfig.getAppPassword();
	}

	private String getAppUserRole() {
		return simbaRestConfig.getAppUserRole();
	}
}
