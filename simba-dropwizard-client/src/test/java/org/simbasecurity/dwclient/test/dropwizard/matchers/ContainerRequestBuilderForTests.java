package org.simbasecurity.dwclient.test.dropwizard.matchers;

import static javax.ws.rs.core.HttpHeaders.*;

import java.io.InputStream;
import java.net.URI;

import org.eclipse.jetty.http.HttpMethods;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.WebApplication;

public class ContainerRequestBuilderForTests {

	public static final String USER_AGENT_STRING = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36";
	private static final URI REQUEST_URI = URI.create("http://rest.wayneindustries.com/v1/bats");
	private static final URI BASE_URI = URI.create("http://rest.wayneindustries.com");

	private WebApplication webApplication = new WebApplicationImpl();
	private URI baseUri = BASE_URI;
	private URI requestUri = REQUEST_URI;
	private String method = HttpMethods.GET;
	private InBoundHeaders headers;
	private InputStream entity;

	public ContainerRequest build() {
		if (headers == null) {
			addHeader(USER_AGENT, USER_AGENT_STRING);
		}
		ContainerRequest containerRequest = new ContainerRequest(webApplication, method, baseUri, requestUri, headers, entity);
		return containerRequest;
	}

	public ContainerRequestBuilderForTests withWebApplication(WebApplication webApplication) {
		this.webApplication = webApplication;
		return this;
	}

	public ContainerRequestBuilderForTests withHttpMethod(String method) {
		this.method = method;
		return this;
	}

	public ContainerRequestBuilderForTests withBaseUri(URI baseUri) {
		this.baseUri = baseUri;
		return this;
	}

	public ContainerRequestBuilderForTests withRequestUri(URI requestUri) {
		this.requestUri = requestUri;
		return this;
	}

	public ContainerRequestBuilderForTests withHeaders(InBoundHeaders headers) {
		this.headers = headers;
		return this;
	}

	/**
	 * Will override existing value of key if it's found<br/>
	 * Will create new headers if empty
	 */
	public ContainerRequestBuilderForTests addHeader(String key, String value) {
		if (headers == null) {
			headers = new InBoundHeaders();
		}
		if (headers.containsKey(key)) {
			headers.remove(key);
		}
		headers.add(key, value);
		return this;
	}

	public ContainerRequestBuilderForTests withEmptyHeaders() {
		this.headers = new InBoundHeaders();
		return this;
	}

	public ContainerRequestBuilderForTests withEntity(InputStream entity) {
		this.entity = entity;
		return this;
	}

	public ContainerRequestBuilderForTests withCookie(String key, String value) {
		addHeader(COOKIE, key + "=" + value + ";");
		return this;
	}

}
