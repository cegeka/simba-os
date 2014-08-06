package org.simbasecurity.dwclient.gateway.resources.users;

import static org.fest.assertions.api.Assertions.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.simbasecurity.dwclient.gateway.representations.SimbaUserR;
import org.simbasecurity.dwclient.test.rule.SimbaDatabaseRule;
import org.simbasecurity.dwclient.test.rule.SimbaManagerRule;

import com.yammer.dropwizard.config.ConfigurationException;

public class SimbaUserServiceTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public SimbaManagerRule simbaManagerRule = SimbaManagerRule.create();

	@Rule
	public SimbaDatabaseRule simbaDatabaseRule = SimbaDatabaseRule.create();

	private SimbaUserService simbaUserService;

	@Before
	public void setUp() throws IOException, ConfigurationException {
		simbaUserService = new SimbaUserService(simbaManagerRule.getSimbaWebResource());
		simbaDatabaseRule.deleteAllUsersExcept(simbaManagerRule.getAppUser());
	}

	@Test
	public void findUserByName_WhenNoUserFound_ThrowsIllegalArgumentException() throws Exception {
		String username = "user";
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(String.format("No user found for name %s.", username));

		simbaUserService.findUserByName(getValidSSOToken(), username);
	}

	@Test
	public void findUserByName_WhenUserFound_ReturnsSimbaUser() throws Exception {
		String username = "bruce@wayneindustries.com";
		simbaDatabaseRule.createUser(username);

		SimbaUserR simbaUser = simbaUserService.findUserByName(getValidSSOToken(), username);

		assertThat(simbaUser.getUserName()).isEqualTo(username);
	}

	private String getValidSSOToken() {
		return simbaManagerRule.getSsoToken().get();
	}
}
