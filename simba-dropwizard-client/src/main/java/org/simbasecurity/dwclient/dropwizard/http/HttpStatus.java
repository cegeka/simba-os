package org.simbasecurity.dwclient.dropwizard.http;

public enum HttpStatus {
	UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
	INTERNAL_SERVER_ERROR(500, "Unknown error occurred."),
	NOT_FOUND(404, "Not Found"),
	BAD_REQUEST(400, "Bad Request"),
	UNAUTHORIZED(401, "Unauthorized");

	private int code;
	private String description;

	private HttpStatus(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
