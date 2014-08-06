package org.simbasecurity.dwclient.gateway.representations;

import org.simbasecurity.dwclient.gateway.protocol.ESAPIDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class SimbaRoleR {

	private long id;
	private int version;
	@JsonDeserialize(using = ESAPIDeserializer.class)
	private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
