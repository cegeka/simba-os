package org.simbasecurity.dwclient.gateway.resources.roles;

import static org.fest.assertions.api.Assertions.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.simbasecurity.dwclient.exception.SimbaUnavailableException;
import org.simbasecurity.dwclient.gateway.representations.SimbaRoleR;
import org.simbasecurity.dwclient.gateway.representations.SimbaUserR;
import org.simbasecurity.dwclient.gateway.resources.users.SimbaUserService;
import org.simbasecurity.dwclient.test.rule.SimbaDatabaseRule;
import org.simbasecurity.dwclient.test.rule.SimbaManagerRule;

import com.yammer.dropwizard.config.ConfigurationException;

public class SimbaRoleServiceTest {

	private static final String DUMMY_TEST_USER = "dummy-test@user.com";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public SimbaManagerRule simbaManagerRule = SimbaManagerRule.create();

	@Rule
	public SimbaDatabaseRule simbaDatabaseRule = SimbaDatabaseRule.create();

	private SimbaRoleService simbaRoleService;
	private SimbaUserService simbaUserService;

	@Before
	public void setUp() throws SimbaUnavailableException, IOException, ConfigurationException {
		simbaRoleService = new SimbaRoleService(simbaManagerRule.getSimbaWebResource());
		simbaUserService = new SimbaUserService(simbaManagerRule.getSimbaWebResource());
		simbaDatabaseRule.deleteAllUsersExcept(simbaManagerRule.getAppUser());
		simbaDatabaseRule.createUser(DUMMY_TEST_USER);
	}

	@Test
	public void findRoleByName_WhenRoleFoundInSimbaRoles_ReturnsFoundRole() throws Exception {
		String anExistingRolename = "simba-manager";
		SimbaRoleR actual = simbaRoleService.findRoleByName(getValidSSOToken(), anExistingRolename);

		assertThat(actual.getName()).isEqualTo(anExistingRolename);
		assertThat(actual.getId()).isNotNull();
		assertThat(actual.getVersion()).isNotNull();
	}

	@Test
	public void findRoleByName_WhenNoRoleFoundInSimbaRoles_ThrowsIllegalArgumentException() throws Exception {
		String rolename = "some unexisting role";

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(String.format("No role found for name %s.", rolename));

		simbaRoleService.findRoleByName(getValidSSOToken(), rolename);
	}

	@Test
	public void addRoleToUser_WhenRoleIsNull_ThrowsIllegalArgumentException() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Role cannot be null.");

		simbaRoleService.addRoleToUser(getValidSSOToken(), null, new SimbaUserR());
	}

	@Test
	public void addRoleToUser_WhenUserIsNull_ThrowsIllegalArgumentException() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("User cannot be null.");

		simbaRoleService.addRoleToUser(getValidSSOToken(), new SimbaRoleR(), null);
	}

	@Test
	public void removeRoleFromUser_WhenRoleIsNull_ThrowsIllegalArgumentException() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Role cannot be null.");
		simbaRoleService.removeRoleFromUser(getValidSSOToken(), null, new SimbaUserR());
	}

	@Test
	public void removeRoleFromUser_WhenUserIsNull_ThrowsIllegalArgumentException() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("User cannot be null.");
		simbaRoleService.removeRoleFromUser(getValidSSOToken(), new SimbaRoleR(), null);
	}

	@Test
	public void addRoleToUser_WhenBothRoleAndUserExist_UserHasRole() throws Exception {
		String anExistingRolename = "simba-manager";
		SimbaRoleR simbaRole = simbaRoleService.findRoleByName(getValidSSOToken(), anExistingRolename);
		SimbaUserR user = simbaUserService.findUserByName(getValidSSOToken(), DUMMY_TEST_USER);

		simbaRoleService.addRoleToUser(getValidSSOToken(), simbaRole, user);

		simbaDatabaseRule.assertUserRoleExistsAndCleanUp(DUMMY_TEST_USER, anExistingRolename);
	}

	@Test
	public void removeRoleFromUser_WhenBothRoleAndUserNotNull_RoleWasRemovedFromUser() throws Exception {
		String anExistingRolename = "simba-manager";
		SimbaRoleR simbaRole = simbaRoleService.findRoleByName(getValidSSOToken(), anExistingRolename);
		SimbaUserR user = simbaUserService.findUserByName(getValidSSOToken(), DUMMY_TEST_USER);

		simbaRoleService.addRoleToUser(getValidSSOToken(), simbaRole, user);
		simbaDatabaseRule.assertUserRoleExists(DUMMY_TEST_USER, anExistingRolename);

		// get them again because otherwise we get OptimizedLockException (since the version of the User and Role was updated after assigning the role)
		simbaRole = simbaRoleService.findRoleByName(getValidSSOToken(), anExistingRolename);
		user = simbaUserService.findUserByName(getValidSSOToken(), DUMMY_TEST_USER);

		simbaRoleService.removeRoleFromUser(getValidSSOToken(), simbaRole, user);
		simbaDatabaseRule.assertUserRoleDoesNotExist(DUMMY_TEST_USER, anExistingRolename);
	}

	private String getValidSSOToken() {
		return simbaManagerRule.getSsoToken().get();
	}

}
