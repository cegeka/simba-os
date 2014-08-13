package org.simbasecurity.dwclient.dropwizard.healthcheck;

import javax.inject.Inject;

import org.simbasecurity.dwclient.gateway.SimbaManagerDBGateway;

import com.yammer.metrics.core.HealthCheck;

public class SimbaManagerDBHealthCheck extends HealthCheck {

	private SimbaManagerDBGateway simbaManagerGateway;

	@Inject
	public SimbaManagerDBHealthCheck(SimbaManagerDBGateway simbaManagerDBGateway) {
		super("simba manager");
		this.simbaManagerGateway = simbaManagerDBGateway;
	}

	@Override
	protected Result check() throws Exception {
		return simbaManagerGateway.isSimbaManagerAlive()
				? Result.healthy("Simba db might be down though, see hibernate health check")
				: Result.unhealthy("Simba Manager is down");
	}

}
