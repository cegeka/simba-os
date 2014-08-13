package org.simbasecurity.dwclient.dropwizard.credentials;

import org.simbasecurity.dwclient.dropwizard.credentials.SimbaPrincipal;

public class SimbaPrincipalBuilderForTests {

	public static final String USERNAME = "bruce@wayneindustries.com";
	public static final String SSO_TOKEN = "7351857301";

	private String ssoToken;
	private String username;

	public SimbaPrincipal build() {
		return new SimbaPrincipal(username, ssoToken);
	}

	public SimbaPrincipalBuilderForTests withDefaults() {
		return new SimbaPrincipalBuilderForTests()
				.withSSOToken(SSO_TOKEN)
				.withUsername(USERNAME);
	}

	public SimbaPrincipalBuilderForTests withSSOToken(String ssoToken) {
		this.ssoToken = ssoToken;
		return this;
	}

	public SimbaPrincipalBuilderForTests withUsername(String username) {
		this.username = username;
		return this;
	}

}
