package org.simbasecurity.dwclient.gateway.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public abstract class AbstractSimbaService {

	private WebResource webResource;

	protected AbstractSimbaService(WebResource webResource) {
		this.webResource = webResource;
	}

	public WebResource getSimbaResource() {
		return webResource;
	}

	protected void handleError(String action, String username, ClientResponse clientResponse, Logger logger) {
		handleError(action, null, username, clientResponse, logger);
	}

	protected void handleError(String action, String rolename, String username, ClientResponse clientResponse, Logger logger) {
		clientResponse.bufferEntity();
		int status = clientResponse.getStatus();
		if (status >= 300) {
			String message = clientResponse.getEntity(String.class);
			logger.error("Action {}\nRolename {}\nUsername {}\nStatus {}\nMessage {}", action, rolename, username, status, message);
			throw new WebApplicationException(Response.status(status).entity(message).build());
		}
	}
}
