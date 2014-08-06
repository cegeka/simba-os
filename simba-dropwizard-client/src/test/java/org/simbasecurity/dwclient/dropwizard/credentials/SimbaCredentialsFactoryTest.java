package org.simbasecurity.dwclient.dropwizard.credentials;

import static javax.ws.rs.core.HttpHeaders.*;
import static javax.ws.rs.core.Response.Status.*;
import static org.fest.assertions.api.Assertions.*;
import static org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsBuilderForTests.*;
import static org.simbasecurity.dwclient.dropwizard.http.HttpHeaders.*;

import java.net.URI;
import java.util.Map;

import org.eclipse.jetty.http.HttpMethods;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.common.request.RequestUtil;
import org.simbasecurity.dwclient.test.dropwizard.matchers.ContainerRequestBuilderForTests;
import org.simbasecurity.dwclient.test.dropwizard.matchers.WebApplicationExceptionMatcher;
import org.simbasecurity.dwclient.test.rule.MockitoRule;

import com.google.common.collect.Maps;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;

public class SimbaCredentialsFactoryTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	@Rule
	public MockitoRule mockitoRule = MockitoRule.create();

	private static final String SIMBA_WEB_URL = "http://simba.wayneindustries.com/simba";

	private SimbaCredentialsFactory factory;

	@Before
	public void setUp() {
		factory = new SimbaCredentialsFactory(SIMBA_WEB_URL);
	}

	@Test
	public void create_BasicAuthentication_DecodesAndCreatesCredentials() throws Exception {
		String username = "emanresu";
		String password = "drowssap";
		String digest = username + ":" + password;
		byte[] encodedDigest = Base64.encode(digest);
		String basicAuthString = "basic " + new String(encodedDigest);

		ContainerRequest containerRequest = new ContainerRequestBuilderForTests()
				.addHeader(AUTHORIZATION, basicAuthString)
				.withRequestUri(URI.create("http://rest.wayneindustries.com/v1/bats?format=timeseries"))
				.withHttpMethod(HttpMethods.GET)
				.build();

		SimbaCredentials expected = new SimbaCredentialsBuilderForTests()
				.withHttpMethod(HttpMethods.GET)
				.addHeader(AUTHORIZATION, basicAuthString)
				.addParameter(AuthenticationConstants.USERNAME, username)
				.addParameter(AuthenticationConstants.PASSWORD, password)
				.addParameter("format", "timeseries")
				.withRequestUrl("http://rest.wayneindustries.com/v1/bats/")
				.withSimbaWebURL(SIMBA_WEB_URL)
				.withHostServerName(RequestUtil.HOST_SERVER_NAME)
				.withIsLoginRequest(true)
				.build();

		SimbaCredentials simbaCredentials = factory.create(containerRequest);
		assertThat(simbaCredentials).isEqualTo(expected);
	}

	@Test
	public void create_SSOTokenIsMappedProperly() throws Exception {
		String token = "55687";
		ContainerRequest containerRequest = new ContainerRequestBuilderForTests()
				.withRequestUri(URI.create(REQUESTURL))
				.addHeader(X_SSO_TOKEN, token)
				.withHttpMethod(HttpMethods.GET)
				.build();

		Map<String, String> requestParameters = Maps.newHashMap();

		SimbaCredentials expected = new SimbaCredentialsBuilderForTests()
				.withRequestUrl(REQUESTURL)
				.addHeader(X_SSO_TOKEN, token)
				.withRequestParameters(requestParameters)
				.withHostServerName(RequestUtil.HOST_SERVER_NAME)
				.withSsotoken(token)
				.build();

		SimbaCredentials actual = factory.create(containerRequest);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void create_NeitherSSOTokenNorBasicAuthentication_Throws401() throws Exception {
		ContainerRequest containerRequest = new ContainerRequestBuilderForTests()
				.withRequestUri(URI.create("http://rest.wayneindustries.com/v1/bats?format=timeseries"))
				.withHttpMethod(HttpMethods.GET)
				.build();

		expectedException.expect(WebApplicationExceptionMatcher.webApplicationException(UNAUTHORIZED));

		factory.create(containerRequest);
	}

	@Test
	public void createForLogin_CreatesCorrectSimbaCredentials() throws Exception {
		String username = "username";
		String password = "password";

		SimbaCredentials expected = new SimbaCredentialsBuilderForTests()
				.addParameter(AuthenticationConstants.USERNAME, username)
				.addParameter(AuthenticationConstants.PASSWORD, password)
				.addHeader("user-agent", "")
				.withIsLoginRequest(true)
				.withIsLogoutRequest(false)
				.withRequestUrl("")
				.withHttpMethod(HttpMethods.POST)
				.withHostServerName(RequestUtil.HOST_SERVER_NAME)
				.build();

		SimbaCredentials actual = factory.createForLogin(username, password);

		assertThat(actual).isEqualTo(expected);
	}

}
