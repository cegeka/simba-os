package org.simbasecurity.dwclient.exception.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.simbasecurity.dwclient.dropwizard.http.HttpStatus;
import org.simbasecurity.dwclient.dropwizard.representation.DWSimbaErrorR;
import org.simbasecurity.dwclient.exception.SimbaUnavailableException;

public class SimbaUnavailableExceptionToJSONMapper implements ExceptionMapper<SimbaUnavailableException> {

	public static final String SIMBA_ERROR_MESSAGE = "Simba unavailable exception was thrown.";

	@Override
	public Response toResponse(SimbaUnavailableException exception) {
		DWSimbaErrorR simbaError = new DWSimbaErrorR(
				HttpStatus.INTERNAL_SERVER_ERROR.getDescription(),
				SIMBA_ERROR_MESSAGE,
				null);

		return Response
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.type(MediaType.APPLICATION_JSON)
				.entity(simbaError)
				.build();
	}

}
