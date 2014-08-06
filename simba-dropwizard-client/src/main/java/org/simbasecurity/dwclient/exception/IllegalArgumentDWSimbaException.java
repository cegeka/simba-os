package org.simbasecurity.dwclient.exception;

import org.simbasecurity.dwclient.dropwizard.http.HttpStatus;

public class IllegalArgumentDWSimbaException extends AbstractDWSimbaException {

	private static final long serialVersionUID = -5349258230481427595L;

	public IllegalArgumentDWSimbaException(String message) {
		super(HttpStatus.BAD_REQUEST, DWSimbaError.ILLEGAL_ARGUMENT, message);
	}

}
