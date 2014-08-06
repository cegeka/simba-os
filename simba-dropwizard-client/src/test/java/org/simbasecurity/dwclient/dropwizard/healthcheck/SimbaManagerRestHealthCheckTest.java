package org.simbasecurity.dwclient.dropwizard.healthcheck;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.simbasecurity.dwclient.dropwizard.healthcheck.SimbaManagerRestHealthCheck;
import org.simbasecurity.dwclient.gateway.SimbaManagerRestGateway;
import org.simbasecurity.dwclient.test.rule.MockitoRule;

import com.yammer.metrics.core.HealthCheck.Result;

public class SimbaManagerRestHealthCheckTest {

	@Rule
	public MockitoRule mockitoRule = MockitoRule.create();

	@Mock
	private SimbaManagerRestGateway simbaManagerRestGatewayMock;

	private SimbaManagerRestHealthCheck simbaHealthCheck;

	@Before
	public void setUp() {
		simbaHealthCheck = new SimbaManagerRestHealthCheck(simbaManagerRestGatewayMock);
	}

	@Test
	public void check_WhenSimbaIsAlive_ReturnsHealthy() throws Exception {
		when(simbaManagerRestGatewayMock.isSimbaRestManagerAlive()).thenReturn(true);
		Result actual = simbaHealthCheck.check();
		assertThat(actual).isEqualTo(Result.healthy());
	}

	@Test
	public void check_WhenSimbaIsNotAlive_ReturnsUnhealthy() throws Exception {
		when(simbaManagerRestGatewayMock.isSimbaRestManagerAlive()).thenReturn(false);
		Result actual = simbaHealthCheck.check();
		assertThat(actual).isEqualTo(Result.unhealthy("Simba Rest Manager is down"));
	}
}
