package org.simbasecurity.dwclient.dropwizard.healthcheck;

import javax.inject.Inject;

import org.simbasecurity.dwclient.gateway.SimbaGateway;

import com.yammer.metrics.core.HealthCheck;

public class SimbaHealthCheck extends HealthCheck {

	private SimbaGateway simbaGateway;

	@Inject
	public SimbaHealthCheck(SimbaGateway simbaGateway) {
		super("simba");
		this.simbaGateway = simbaGateway;
	}

	@Override
	protected Result check() throws Exception {
		if (simbaGateway.isSimbaAlive()) {
			return Result.healthy();
		}
		return Result.unhealthy("Could not establish connection");
	}

}
