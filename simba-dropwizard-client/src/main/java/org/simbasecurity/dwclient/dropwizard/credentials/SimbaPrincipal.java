package org.simbasecurity.dwclient.dropwizard.credentials;

import com.google.common.base.Objects;

public class SimbaPrincipal {

	private String username;
	private String ssoToken;

	public SimbaPrincipal(String username, String ssoToken) {
		this.username = username;
		this.ssoToken = ssoToken;
	}

	public String getUsername() {
		return username;
	}

	public String getSsoToken() {
		return ssoToken;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(username, ssoToken);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().isAssignableFrom(this.getClass())) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		SimbaPrincipal other = (SimbaPrincipal) obj;
		return Objects.equal(this.username, other.username)
				&& Objects.equal(this.ssoToken, other.ssoToken);
	}
}
