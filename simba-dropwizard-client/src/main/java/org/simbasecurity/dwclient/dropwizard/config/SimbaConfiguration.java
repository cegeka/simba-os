package org.simbasecurity.dwclient.dropwizard.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimbaConfiguration {

	@NotEmpty
	@JsonProperty
	private String simbaWebURL = "http://localhost:8080/simba/";

	@NotEmpty
	@JsonProperty
	private String filterPath = "/*";

	public String getSimbaWebURL() {
		return simbaWebURL;
	}

	public String getFilterPath() {
		return filterPath;
	}
}
