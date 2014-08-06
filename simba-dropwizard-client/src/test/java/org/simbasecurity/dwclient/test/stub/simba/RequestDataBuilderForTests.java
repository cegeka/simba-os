package org.simbasecurity.dwclient.test.stub.simba;

import java.util.Map;

import org.eclipse.jetty.http.HttpMethods;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.api.service.thrift.SSOToken;

public class RequestDataBuilderForTests {

	private String simbaWebURL = "http://simba.wayneindustries.com:8087/simba";
	private Map<String, String> requestParameters;
	private Map<String, String> requestHeaders;
	private String requestUrl;
	private String requestMethod = HttpMethods.GET;
	private String hostServerName;
	private String loginToken;

	// currently unused
	private SSOToken ssoToken;
	private String clientIP;
	private boolean logoutRequest = false;
	private boolean loginRequest = false;
	private boolean ssoTokenMappingKeyProvided = false;
	private boolean changePasswordRequest = false;
	private boolean showChangePasswordRequest = false;

	public RequestDataBuilderForTests() {
	}

	public RequestData build() {
		RequestData result = new RequestData(requestParameters, requestHeaders,
				requestUrl, simbaWebURL, ssoToken, clientIP,
				logoutRequest, loginRequest, ssoTokenMappingKeyProvided, changePasswordRequest, showChangePasswordRequest,
				requestMethod, hostServerName, loginToken);
		return result;
	}

	public RequestDataBuilderForTests withSimbaWebURL(String simbaWebURL) {
		this.simbaWebURL = simbaWebURL;
		return this;
	}

	public RequestDataBuilderForTests withRequestParameters(Map<String, String> requestParameters) {
		this.requestParameters = requestParameters;
		return this;
	}

	public RequestDataBuilderForTests withRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
		return this;
	}

	public RequestDataBuilderForTests withRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
		return this;
	}

	public RequestDataBuilderForTests withRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
		return this;
	}

	public RequestDataBuilderForTests withHostServerName(String hostServerName) {
		this.hostServerName = hostServerName;
		return this;
	}

	public RequestDataBuilderForTests withLoginToken(String loginToken) {
		this.loginToken = loginToken;
		return this;
	}

	public RequestDataBuilderForTests withSsoToken(SSOToken ssoToken) {
		this.ssoToken = ssoToken;
		return this;
	}

	public RequestDataBuilderForTests withClientIP(String clientIP) {
		this.clientIP = clientIP;
		return this;
	}

	public RequestDataBuilderForTests withLogoutRequest(boolean logoutRequest) {
		this.logoutRequest = logoutRequest;
		return this;
	}

	public RequestDataBuilderForTests withLoginRequest(boolean loginRequest) {
		this.loginRequest = loginRequest;
		return this;
	}

	public RequestDataBuilderForTests withSsoTokenMappingKeyProvided(boolean ssoTokenMappingKeyProvided) {
		this.ssoTokenMappingKeyProvided = ssoTokenMappingKeyProvided;
		return this;
	}

	public RequestDataBuilderForTests withChangePasswordRequest(boolean changePasswordRequest) {
		this.changePasswordRequest = changePasswordRequest;
		return this;
	}

	public RequestDataBuilderForTests withShowChangePasswordRequest(boolean showChangePasswordRequest) {
		this.showChangePasswordRequest = showChangePasswordRequest;
		return this;
	}

}
