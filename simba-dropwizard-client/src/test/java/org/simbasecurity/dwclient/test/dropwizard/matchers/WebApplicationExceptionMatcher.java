package org.simbasecurity.dwclient.test.dropwizard.matchers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class WebApplicationExceptionMatcher extends TypeSafeMatcher<WebApplicationException> {

	private Status expectedStatus;

	private WebApplicationExceptionMatcher(Status expectedStatus) {
		this.expectedStatus = expectedStatus;
	}

	public static WebApplicationExceptionMatcher webApplicationException(Status expectedStatus) {
		return new WebApplicationExceptionMatcher(expectedStatus);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(String.format("statuscode %s", expectedStatus.getStatusCode()));
	}

	@Override
	protected void describeMismatchSafely(WebApplicationException item, Description mismatchDescription) {
		mismatchDescription.appendText(String.format("was statuscode %s", item.getResponse().getStatus()));
	}

	@Override
	protected boolean matchesSafely(WebApplicationException exception) {
		return exception.getResponse().getStatus() == expectedStatus.getStatusCode();
	}

}