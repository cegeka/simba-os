package org.simbasecurity.dwclient.test.rule;

import static org.fest.assertions.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.simbasecurity.dwclient.dropwizard.config.SimbaManagerRestConfiguration;
import org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsFactory;
import org.simbasecurity.dwclient.exception.SimbaUnavailableException;
import org.simbasecurity.dwclient.gateway.SimbaGateway;
import org.simbasecurity.dwclient.gateway.SimbaServiceFactory;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.WebResource;
import com.yammer.dropwizard.client.JerseyClientBuilder;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.ConfigurationException;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.json.ObjectMapperFactory;
import com.yammer.dropwizard.validation.Validator;

public class SimbaManagerRule implements MethodRule {

	private WebResource simbaWebResource;
	private String simbaMgrRestConfigFile;
	private Optional<String> ssoToken;

	public static SimbaManagerRule create(String simbaManagerRestConfigurationFile) {
		return new SimbaManagerRule(simbaManagerRestConfigurationFile);
	}

	public static SimbaManagerRule create() {
		return new SimbaManagerRule("src/test/resources/test-simba-rest-mgr.yml");
	}

	private SimbaManagerRule(String simbaManagerRestConfigurationFile) {
		this.simbaMgrRestConfigFile = simbaManagerRestConfigurationFile;
	}

	@Override
	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				before();
				try {
					base.evaluate();
				} finally {
					after();
				}
			}
		};
	}

	public WebResource getSimbaWebResource() {
		return simbaWebResource;
	}

	public Optional<String> getSsoToken() {
		return ssoToken;
	}

	private void before() throws IOException, ConfigurationException, SimbaUnavailableException {
		SimbaManagerRestConfiguration simbaRestMgrConfig = getSimbaManagerRestConfiguration();
		simbaWebResource = getSimbaWebResource(simbaRestMgrConfig);
		String simbaWebURL = simbaRestMgrConfig.getSimbaWebURL();
		SimbaGateway simbaGateway = new SimbaGateway(simbaWebURL, new SimbaServiceFactory(), new SimbaCredentialsFactory(
				simbaWebURL));
		String appUser = simbaRestMgrConfig.getAppUser();
		String appPassword = simbaRestMgrConfig.getAppPassword();
		ssoToken = simbaGateway.login(appUser, appPassword);
		if (!ssoToken.isPresent()) {
			fail(String.format("Unsuccessful login for configured user/password: %s/%s", appUser, appPassword));
		}
	}

	public String getAppUser() throws IOException, ConfigurationException {
		return getSimbaManagerRestConfiguration().getAppUser();
	}

	private SimbaManagerRestConfiguration getSimbaManagerRestConfiguration() throws IOException, ConfigurationException {
		return ConfigurationFactory.forClass(SimbaManagerRestConfiguration.class, new Validator()).build(new File(simbaMgrRestConfigFile));
	}

	private WebResource getSimbaWebResource(SimbaManagerRestConfiguration config) {
		ObjectMapperFactory objectMapperFactory = new ObjectMapperFactory();
		Environment env = new Environment("SimbaRestManager Test Environment", new Configuration(), objectMapperFactory, new Validator());
		return new JerseyClientBuilder()
				.using(env)
				.using(config)
				.build()
				.resource(config.getSimbaManagerURL());
	}

	private void after() {
		// noop
	}

}
