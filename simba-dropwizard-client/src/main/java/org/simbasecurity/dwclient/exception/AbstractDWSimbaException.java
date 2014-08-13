package org.simbasecurity.dwclient.exception;

import java.util.Collections;

import org.simbasecurity.dwclient.dropwizard.http.HttpStatus;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public abstract class AbstractDWSimbaException extends RuntimeException {

	private static final long serialVersionUID = 2843621907770518642L;

	private HttpStatus httpStatus;
	private String errorCode;
	private String errorDescription;
	private Iterable<String> messages;

	protected AbstractDWSimbaException(HttpStatus httpStatus, String errorCode, String errorDescription, Iterable<String> messages) {
		super(messages == null ? null : messages.toString());
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.messages = Lists.newArrayList(messages);
	}

	protected AbstractDWSimbaException(HttpStatus httpStatus, DWSimbaError applicationError, Iterable<String> messages) {
		this(httpStatus, applicationError.getCode(), applicationError.getDescription(), messages);
	}

	protected AbstractDWSimbaException(HttpStatus httpStatus, DWSimbaError applicationError, String message) {
		this(httpStatus, applicationError.getCode(), applicationError.getDescription(), Collections.singletonList(message));
	}

	protected AbstractDWSimbaException(HttpStatus httpStatus, DWSimbaError applicationError) {
		this(httpStatus, applicationError.getCode(), applicationError.getDescription(), Lists.<String> newArrayList());
	}

	public String getGeneralMessage() {
		return errorDescription;
	}

	public String getErrorcode() {
		return errorCode;
	}

	public Iterable<String> getErrors() {
		return messages;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.errorCode, this.httpStatus, this.errorDescription, this.messages);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbstractDWSimbaException)) {
			return false;
		}
		AbstractDWSimbaException other = (AbstractDWSimbaException) obj;
		return Objects.equal(this.errorCode, other.errorCode)
				&& Objects.equal(this.httpStatus, other.httpStatus)
				&& Objects.equal(this.errorDescription, other.errorDescription)
				&& Objects.equal(this.messages, other.messages);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("errorcode", errorCode)
				.add("httpStatus", httpStatus)
				.add("message", errorDescription)
				.add("errors", messages)
				.toString();
	}
}