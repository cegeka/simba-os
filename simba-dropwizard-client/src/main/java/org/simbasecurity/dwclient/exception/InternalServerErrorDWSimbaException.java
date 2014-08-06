package org.simbasecurity.dwclient.exception;

import org.simbasecurity.dwclient.dropwizard.http.HttpStatus;

public class InternalServerErrorDWSimbaException extends AbstractDWSimbaException {

	private static final long serialVersionUID = -7348451635240708810L;

	public InternalServerErrorDWSimbaException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR, DWSimbaError.GENERIC_ERROR);
	}
}
