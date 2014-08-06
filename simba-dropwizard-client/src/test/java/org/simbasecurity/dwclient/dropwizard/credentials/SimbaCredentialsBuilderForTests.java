package org.simbasecurity.dwclient.dropwizard.credentials;

import java.util.Map;

import org.eclipse.jetty.http.HttpMethods;

import com.google.common.collect.Maps;

public class SimbaCredentialsBuilderForTests {

	public static final String HTTP_METHOD = HttpMethods.GET;
	public static final String HOSTSERVERNAME = "hostservername";
	public static final String REQUESTURL = "http://rest.wayneindustries.com/v1/bats/";
	public static final String SIMBAWEBURL = "http://simba.wayneindustries.com/simba";

	private Map<String, String> requestParameters;
	private Map<String, String> requestHeaders;
	private String requestUrl = REQUESTURL;
	private String simbaWebURL = SIMBAWEBURL;
	private String httpMethod = HTTP_METHOD;
	private String hostServerName = HOSTSERVERNAME;
	private String ssotoken;
	private boolean clientIsABrowser;
	private boolean isLoginRequest;
	private String clientIPAddress;
	private boolean isLogoutRequest;

	public SimbaCredentialsBuilderForTests() {
	}

	public SimbaCredentials build() {
		return new SimbaCredentials(requestParameters, requestHeaders, requestUrl, simbaWebURL, httpMethod, ssotoken, hostServerName, clientIsABrowser,
				isLoginRequest, isLogoutRequest, clientIPAddress);
	}

	public SimbaCredentialsBuilderForTests withRequestParameters(Map<String, String> requestParameters) {
		this.requestParameters = requestParameters;
		return this;
	}

	public SimbaCredentialsBuilderForTests withRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
		return this;
	}

	public SimbaCredentialsBuilderForTests withRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
		return this;
	}

	public SimbaCredentialsBuilderForTests withSimbaWebURL(String simbaWebURL) {
		this.simbaWebURL = simbaWebURL;
		return this;
	}

	public SimbaCredentialsBuilderForTests withHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
		return this;
	}

	public SimbaCredentialsBuilderForTests withSsotoken(String ssotoken) {
		this.ssotoken = ssotoken;
		return this;
	}

	public SimbaCredentialsBuilderForTests withHostServerName(String hostServerName) {
		this.hostServerName = hostServerName;
		return this;
	}

	public SimbaCredentialsBuilderForTests withClientIsABrowser(boolean clientIsABrowser) {
		this.clientIsABrowser = clientIsABrowser;
		return this;
	}

	public SimbaCredentialsBuilderForTests addHeader(String key, String value) {
		if (requestHeaders == null) {
			requestHeaders = Maps.newHashMap();
		}
		this.requestHeaders.put(key, value);
		return this;
	}

	public SimbaCredentialsBuilderForTests addParameter(String key, String value) {
		if (requestParameters == null) {
			requestParameters = Maps.newHashMap();
		}
		this.requestParameters.put(key, value);
		return this;
	}

	public SimbaCredentialsBuilderForTests withIsLoginRequest(boolean isLoginRequest) {
		this.isLoginRequest = isLoginRequest;
		return this;
	}

	public SimbaCredentialsBuilderForTests withClientIPAddress(String clientIPAddress) {
		this.clientIPAddress = clientIPAddress;
		return this;
	}

	public SimbaCredentialsBuilderForTests withIsLogoutRequest(boolean isLogoutRequest) {
		this.isLogoutRequest = isLogoutRequest;
		return this;
	}

}
