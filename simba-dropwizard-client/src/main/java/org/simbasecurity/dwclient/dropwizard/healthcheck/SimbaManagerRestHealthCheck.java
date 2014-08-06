package org.simbasecurity.dwclient.dropwizard.healthcheck;

import javax.inject.Inject;

import org.simbasecurity.dwclient.gateway.SimbaManagerRestGateway;

import com.yammer.metrics.core.HealthCheck;

public class SimbaManagerRestHealthCheck extends HealthCheck {

	private SimbaManagerRestGateway simbaManagerRestGateway;

	@Inject
	public SimbaManagerRestHealthCheck(SimbaManagerRestGateway simbaManagerRestGateway) {
		super("simba manager");
		this.simbaManagerRestGateway = simbaManagerRestGateway;
	}

	@Override
	protected Result check() throws Exception {
		return simbaManagerRestGateway.isSimbaRestManagerAlive() ? Result.healthy() : Result.unhealthy("Simba Rest Manager is down");
	}

}
