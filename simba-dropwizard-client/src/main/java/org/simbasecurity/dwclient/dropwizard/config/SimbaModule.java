package org.simbasecurity.dwclient.dropwizard.config;

import java.util.List;

import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.simbasecurity.dwclient.dropwizard.authenticator.SimbaAuthenticator;
import org.simbasecurity.dwclient.dropwizard.healthcheck.SimbaHealthCheck;
import org.simbasecurity.dwclient.dropwizard.healthcheck.SimbaManagerDBHealthCheck;
import org.simbasecurity.dwclient.dropwizard.healthcheck.SimbaManagerRestHealthCheck;
import org.simbasecurity.dwclient.gateway.SimbaGateway;
import org.simbasecurity.dwclient.gateway.SimbaManagerDBGateway;
import org.simbasecurity.dwclient.gateway.SimbaManagerRestGateway;
import org.simbasecurity.dwclient.gateway.SimbaServiceFactory;
import org.simbasecurity.dwclient.gateway.resources.roles.SimbaRoleService;
import org.simbasecurity.dwclient.gateway.resources.users.SimbaUserService;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.yammer.dropwizard.client.JerseyClientBuilder;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.SessionFactoryFactory;
import com.yammer.dropwizard.json.ObjectMapperFactory;
import com.yammer.dropwizard.validation.Validator;

public class SimbaModule extends AbstractModule {

	private List<Class<?>> simbentities;

	public SimbaModule(List<Class<?>> simbentities) {
		this.simbentities = simbentities;
	}

	@Override
	protected void configure() {
		requireBinding(SimbaConfiguration.class);
		requireBinding(SimbaManagerRestConfiguration.class);

		bind(SimbaServiceFactory.class).in(Singleton.class);
		bind(SimbaGateway.class).in(Singleton.class);
		bind(SimbaAuthenticator.class).in(Singleton.class);

		bind(SimbaManagerDBGateway.class).in(Singleton.class);

		bind(SimbaManagerRestGateway.class).in(Singleton.class);
		bind(SimbaRoleService.class).in(Singleton.class);
		bind(SimbaUserService.class).in(Singleton.class);

		bind(SimbaHealthCheck.class).in(Singleton.class);
		bind(SimbaManagerRestHealthCheck.class).in(Singleton.class);
		bind(SimbaManagerDBHealthCheck.class).in(Singleton.class);
	}

	@Provides
	@Singleton
	@Named("simbaWebURL")
	public String provideSimbaWebURL(SimbaConfiguration config) {
		return config.getSimbaWebURL();
	}

	@Provides
	@Singleton
	@Named("simbaManagerAppUser")
	public String provideSimbaManagerAppUser(SimbaManagerRestConfiguration config) {
		return config.getAppUser();
	}

	@Provides
	@Singleton
	@Named("simbaManagerAppPassword")
	public String provideSimbaManagerAppPassword(SimbaManagerRestConfiguration config) {
		return config.getAppPassword();
	}

	@Provides
	@Singleton
	@Named("simbaSessionFactory")
	public SessionFactory provideSessionFactory(@Named("simbaDatabaseConfiguration") DatabaseConfiguration configuration, Environment environment)
			throws ClassNotFoundException {
		return new SessionFactoryFactory().build(environment, configuration, simbentities);
	}

	@Provides
	@Singleton
	@Named("simbaManagerWebResource")
	public WebResource provideSimbaManagerWebResource(SimbaManagerRestConfiguration config) {
		return getClient(config).resource(config.getSimbaManagerURL());
	}

	private Client getClient(SimbaManagerRestConfiguration config) {
		return new JerseyClientBuilder()
				.using(getEnv())
				.using(config)
				.build();
	}

	private Environment getEnv() {
		return new Environment("SimbaRestManager Environment", new Configuration(), new ObjectMapperFactory(), new Validator());
	}
}
