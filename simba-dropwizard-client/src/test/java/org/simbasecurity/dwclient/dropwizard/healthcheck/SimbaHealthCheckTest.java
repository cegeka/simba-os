package org.simbasecurity.dwclient.dropwizard.healthcheck;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.simbasecurity.dwclient.dropwizard.healthcheck.SimbaHealthCheck;
import org.simbasecurity.dwclient.gateway.SimbaGateway;
import org.simbasecurity.dwclient.test.rule.MockitoRule;

import com.yammer.metrics.core.HealthCheck.Result;

public class SimbaHealthCheckTest {

	@Rule
	public MockitoRule mockitoRule = MockitoRule.create();

	@Mock
	private SimbaGateway simbaGatewayMock;

	private SimbaHealthCheck simbaHealthCheck;

	@Before
	public void setUp() {
		simbaHealthCheck = new SimbaHealthCheck(simbaGatewayMock);
	}

	@Test
	public void check_WhenSimbaIsAlive_ReturnsHealthy() throws Exception {
		when(simbaGatewayMock.isSimbaAlive()).thenReturn(true);
		Result actual = simbaHealthCheck.check();
		assertThat(actual).isEqualTo(Result.healthy());
	}

	@Test
	public void check_WhenSimbaIsNotAlive_ReturnsUnhealthy() throws Exception {
		when(simbaGatewayMock.isSimbaAlive()).thenReturn(false);
		Result actual = simbaHealthCheck.check();
		assertThat(actual).isEqualTo(Result.unhealthy("Could not establish connection"));
	}
}
