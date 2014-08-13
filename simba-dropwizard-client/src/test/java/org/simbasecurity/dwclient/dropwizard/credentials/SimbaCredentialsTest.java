package org.simbasecurity.dwclient.dropwizard.credentials;

import static org.fest.assertions.api.Assertions.*;
import static org.simbasecurity.dwclient.dropwizard.credentials.SimbaCredentialsBuilderForTests.*;

import java.util.Map;

import org.eclipse.jetty.http.HttpHeaders;
import org.junit.Test;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.dwclient.test.stub.simba.RequestDataBuilderForTests;

import com.google.common.collect.Maps;

public class SimbaCredentialsTest {

	@Test
	public void asRequestData_ReturnsRequestDataFromInternals() throws Exception {
		Map<String, String> requestHeaders = Maps.newHashMap();
		requestHeaders.put(HttpHeaders.WWW_AUTHENTICATE, "auth-string");
		Map<String, String> requestParameters = Maps.newHashMap();
		requestParameters.put("format", "timeseries");
		SimbaCredentials simbaCredentials = new SimbaCredentialsBuilderForTests()
				.withRequestUrl(SimbaCredentialsBuilderForTests.REQUESTURL + "?format=timeseries")
				.withRequestHeaders(requestHeaders)
				.withRequestParameters(requestParameters)
				.withSsotoken("123456")
				.withIsLoginRequest(true)
				.build();

		RequestData expected = new RequestDataBuilderForTests()
				.withHostServerName(HOSTSERVERNAME)
				.withRequestMethod(HTTP_METHOD)
				.withRequestUrl(REQUESTURL + "?format=timeseries")
				.withSimbaWebURL(SIMBAWEBURL)
				.withRequestHeaders(requestHeaders)
				.withRequestParameters(requestParameters)
				.withSsoToken(new SSOToken("123456"))
				.withLoginRequest(true)
				.build();

		RequestData actual = simbaCredentials.asRequestData();
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void asRequestData_IfEmptySSOTokenOnCredentials_ReturnsRequestDataWithSSOTokenNull() throws Exception {
		Map<String, String> requestHeaders = Maps.newHashMap();
		requestHeaders.put(HttpHeaders.WWW_AUTHENTICATE, "auth-string");
		Map<String, String> requestParameters = Maps.newHashMap();
		requestParameters.put("format", "timeseries");

		SimbaCredentials simbaCredentials = new SimbaCredentialsBuilderForTests()
				.withRequestUrl(SimbaCredentialsBuilderForTests.REQUESTURL + "?format=timeseries")
				.withRequestHeaders(requestHeaders)
				.withRequestParameters(requestParameters)
				.withSsotoken("")
				.build();

		RequestData expected = new RequestDataBuilderForTests()
				.withHostServerName(HOSTSERVERNAME)
				.withRequestMethod(HTTP_METHOD)
				.withRequestUrl(REQUESTURL + "?format=timeseries")
				.withSimbaWebURL(SIMBAWEBURL)
				.withRequestHeaders(requestHeaders)
				.withRequestParameters(requestParameters)
				.withSsoToken(null)
				.build();

		RequestData actual = simbaCredentials.asRequestData();
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void asRequestData_IfNoSSOTokenOnCredentials_ReturnsRequestDataWithSSOTokenNull() throws Exception {
		Map<String, String> requestHeaders = Maps.newHashMap();
		requestHeaders.put(HttpHeaders.WWW_AUTHENTICATE, "auth-string");
		Map<String, String> requestParameters = Maps.newHashMap();
		requestParameters.put("format", "timeseries");

		SimbaCredentials simbaCredentials = new SimbaCredentialsBuilderForTests()
				.withRequestUrl(SimbaCredentialsBuilderForTests.REQUESTURL + "?format=timeseries")
				.withRequestHeaders(requestHeaders)
				.withRequestParameters(requestParameters)
				.withSsotoken(null)
				.build();

		RequestData expected = new RequestDataBuilderForTests()
				.withHostServerName(HOSTSERVERNAME)
				.withRequestMethod(HTTP_METHOD)
				.withRequestUrl(REQUESTURL + "?format=timeseries")
				.withSimbaWebURL(SIMBAWEBURL)
				.withRequestHeaders(requestHeaders)
				.withRequestParameters(requestParameters)
				.withSsoToken(null)
				.build();

		RequestData actual = simbaCredentials.asRequestData();
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void getUserAgentHeader_WhenHeaderIsNull_ReturnEmptyString() throws Exception {
		SimbaCredentials simbaCredentials = new SimbaCredentialsBuilderForTests()
				.withRequestHeaders(null)
				.build();

		assertThat(simbaCredentials.getUserAgentHeader()).isNull();
	}

	@Test
	public void getUserAgentHeader_WhenRequestHeadersIsNull_ReturnNull() throws Exception {
		SimbaCredentials simbaCredentials = new SimbaCredentialsBuilderForTests()
				.withRequestHeaders(null)
				.build();

		assertThat(simbaCredentials.getUserAgentHeader()).isNull();
	}

	@Test
	public void getUserAgentHeader_WhenHeadersDoesNotContainUserAgentHeader_ReturnNull() throws Exception {
		Map<String, String> requestHeaders = Maps.newHashMap();
		requestHeaders.put(HttpHeaders.WWW_AUTHENTICATE, "auth-string");
		SimbaCredentials simbaCredentials = new SimbaCredentialsBuilderForTests()
				.withRequestHeaders(requestHeaders)
				.build();

		assertThat(simbaCredentials.getUserAgentHeader()).isNull();
	}

	@Test
	public void getUserAgentHeader_WhenUserAgentHeaderIsNull_ReturnEmptyString() throws Exception {
		Map<String, String> requestHeaders = Maps.newHashMap();
		requestHeaders.put(HttpHeaders.USER_AGENT, null);
		SimbaCredentials simbaCredentials = new SimbaCredentialsBuilderForTests()
				.withRequestHeaders(requestHeaders)
				.build();

		assertThat(simbaCredentials.getUserAgentHeader()).isNull();
	}

	@Test
	public void getUserAgentHeader_WhenUserAgentHeaderIsNotNull_ReturnUserAgentHeader() throws Exception {
		String userAgentHeader = "mozilla";
		Map<String, String> requestHeaders = Maps.newHashMap();
		requestHeaders.put(HttpHeaders.USER_AGENT, userAgentHeader);
		SimbaCredentials simbaCredentials = new SimbaCredentialsBuilderForTests()
				.withRequestHeaders(requestHeaders)
				.build();

		assertThat(simbaCredentials.getUserAgentHeader()).isEqualTo(userAgentHeader);
	}

}
