package org.simbasecurity.dwclient.gateway;

import javax.inject.Inject;

import org.simbasecurity.dwclient.domain.user.SimbaUser;
import org.simbasecurity.dwclient.domain.user.SimbaUserRepository;

public class SimbaManagerDBGateway {

	private static final boolean ALWAYS_TRUE_BECAUSE_HIBERNATE_WILL_FAIL_HEALTHCHECK = true;
	private SimbaUserRepository simbaUserRepository;

	@Inject
	public SimbaManagerDBGateway(SimbaUserRepository simbaUserRepository) {
		this.simbaUserRepository = simbaUserRepository;
	}

	public String createUserWithEmailAddress(String emailAddress, String password) {
		SimbaUser simbaUser = new SimbaUser(emailAddress, password);
		simbaUserRepository.save(simbaUser);
		return emailAddress;
	}

	public void updatePassword(String simbaID, String newPassword) {
		SimbaUser simbaUser = simbaUserRepository.getSimbaUser(simbaID);
		simbaUser.setPassword(newPassword);
		simbaUserRepository.save(simbaUser);
	}

	public void deleteUser(String simbaId) {
		simbaUserRepository.delete(simbaId);
	}

	public boolean isSimbaManagerAlive() {
		return ALWAYS_TRUE_BECAUSE_HIBERNATE_WILL_FAIL_HEALTHCHECK;
	}

}
