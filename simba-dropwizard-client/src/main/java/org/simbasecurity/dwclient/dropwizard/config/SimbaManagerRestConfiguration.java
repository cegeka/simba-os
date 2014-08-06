package org.simbasecurity.dwclient.dropwizard.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.JerseyClientConfiguration;

public class SimbaManagerRestConfiguration extends JerseyClientConfiguration {

	@NotEmpty
	@JsonProperty
	private String simbaURL = "http://localhost:8087/simba/manager";

	@NotEmpty
	@JsonProperty
	private String simbaWebURL = "http://localhost:8087/simba";

	/**
	 * The role name that gets access to the Simba Manager REST services.
	 */
	@NotEmpty
	@JsonProperty
	private String appUserRole;

	/**
	 * The application user that has the appUserRole so it can access the Simba Manager REST services.
	 */
	@NotEmpty
	@JsonProperty
	private String appUser;

	/**
	 * The application user's password
	 */
	@NotEmpty
	@JsonProperty
	private String appPassword;

	public String getSimbaManagerURL() {
		return simbaURL;
	}

	public void setSimbaURL(String simbaURL) {
		this.simbaURL = simbaURL;
	}

	public String getSimbaWebURL() {
		return simbaWebURL;
	}

	public void setSimbaWebURL(String simbaWebURL) {
		this.simbaWebURL = simbaWebURL;
	}

	public void setAppUserRole(String appUserRole) {
		this.appUserRole = appUserRole;
	}

	public void setAppUser(String appUser) {
		this.appUser = appUser;
	}

	public void setAppPassword(String appPassword) {
		this.appPassword = appPassword;
	}

	public String getAppUserRole() {
		return appUserRole;
	}

	public String getAppUser() {
		return appUser;
	}

	public String getAppPassword() {
		return appPassword;
	}

}
