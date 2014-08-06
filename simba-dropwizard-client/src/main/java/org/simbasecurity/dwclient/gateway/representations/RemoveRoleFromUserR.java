package org.simbasecurity.dwclient.gateway.representations;

public class RemoveRoleFromUserR {

	private SimbaRoleR role;
	private SimbaUserR user;

	public RemoveRoleFromUserR() {
	}

	public RemoveRoleFromUserR(SimbaRoleR role, SimbaUserR user) {
		this.role = role;
		this.user = user;
	}

	public SimbaRoleR getRole() {
		return role;
	}

	public SimbaUserR getUser() {
		return user;
	}

	public void setRole(SimbaRoleR role) {
		this.role = role;
	}

	public void setUser(SimbaUserR user) {
		this.user = user;
	}
}
