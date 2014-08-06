package org.simbasecurity.dwclient.dropwizard.representation;

import java.util.UUID;

import org.simbasecurity.dwclient.dropwizard.http.HttpStatus;
import org.simbasecurity.dwclient.exception.AbstractDWSimbaException;

import com.google.common.base.Objects;

public class DWSimbaErrorR {
	private String title;

	private String message;

	private Iterable<String> errors;

	public DWSimbaErrorR(String title, String message, Iterable<String> errors) {
		this.title = title;
		this.message = message;
		this.errors = errors;
	}

	public static DWSimbaErrorR from(AbstractDWSimbaException exception) {
		return new DWSimbaErrorR("Something went wrong", exception.getGeneralMessage(), exception.getErrors());
	}

	public static DWSimbaErrorR from(Exception genericException) {
		return new DWSimbaErrorR(HttpStatus.INTERNAL_SERVER_ERROR.getDescription(), UUID.randomUUID().toString(), null);
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public Iterable<String> getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("title", title)
				.add("message", message)
				.add("errors", errors)
				.toString();
	}
}
