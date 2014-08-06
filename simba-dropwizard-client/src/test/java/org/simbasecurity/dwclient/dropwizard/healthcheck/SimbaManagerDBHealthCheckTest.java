package org.simbasecurity.dwclient.dropwizard.healthcheck;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.simbasecurity.dwclient.dropwizard.healthcheck.SimbaManagerDBHealthCheck;
import org.simbasecurity.dwclient.gateway.SimbaManagerDBGateway;
import org.simbasecurity.dwclient.test.rule.MockitoRule;

import com.yammer.metrics.core.HealthCheck.Result;

public class SimbaManagerDBHealthCheckTest {

	@Rule
	public MockitoRule mockitoRule = MockitoRule.create();

	@Mock
	private SimbaManagerDBGateway simbaManagerDBGatewayMock;

	private SimbaManagerDBHealthCheck simbaHealthCheck;

	@Before
	public void setUp() {
		simbaHealthCheck = new SimbaManagerDBHealthCheck(simbaManagerDBGatewayMock);
	}

	@Test
	public void check_WhenSimbaIsAlive_ReturnsHealthy() throws Exception {
		when(simbaManagerDBGatewayMock.isSimbaManagerAlive()).thenReturn(true);
		Result actual = simbaHealthCheck.check();
		assertThat(actual).isEqualTo(Result.healthy("Simba db might be down though, see hibernate health check"));
	}

	@Test
	public void check_WhenSimbaIsNotAlive_ReturnsUnhealthy() throws Exception {
		when(simbaManagerDBGatewayMock.isSimbaManagerAlive()).thenReturn(false);
		Result actual = simbaHealthCheck.check();
		assertThat(actual).isEqualTo(Result.unhealthy("Simba Manager is down"));
	}
}
