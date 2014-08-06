package org.simbasecurity.dwclient.gateway.representations;

import java.util.List;

import com.google.common.collect.Lists;

public class AddRoleToUsersR {

	private SimbaRoleR role;
	private List<SimbaUserR> users;

	public AddRoleToUsersR() {
	}

	public AddRoleToUsersR(SimbaRoleR simbaRole, SimbaUserR simbaUser) {
		role = simbaRole;
		users = Lists.newArrayList(simbaUser);
	}

	public SimbaRoleR getRole() {
		return role;
	}

	public void setRole(SimbaRoleR role) {
		this.role = role;
	}

	public List<SimbaUserR> getUsers() {
		return users;
	}

	public void setUsers(List<SimbaUserR> users) {
		this.users = users;
	}

}
