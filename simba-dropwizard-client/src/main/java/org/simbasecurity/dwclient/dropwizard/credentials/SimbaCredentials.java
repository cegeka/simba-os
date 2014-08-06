package org.simbasecurity.dwclient.dropwizard.credentials;

import java.util.Map;

import org.eclipse.jetty.http.HttpHeaders;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.api.service.thrift.SSOToken;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * Wrapper for Simba's RequestData
 */
public class SimbaCredentials {
	private Map<String, String> requestParameters;
	private Map<String, String> requestHeaders;
	private String requestUrl;
	private String simbaWebURL;
	private String httpMethod;
	private String hostServerName;
	private boolean clientIsABrowser;
	private String ssoToken;
	private boolean isLoginRequest;
	private String clientIPAddress;
	private boolean isLogoutRequest;

	SimbaCredentials(Map<String, String> requestParameters, Map<String, String> requestHeaders, String requestUrl, String simbaWebURL,
			String httpMethod, String ssoToken, String hostServerName, boolean clientIsABrowser, boolean isLoginRequest, boolean isLogoutRequest,
			String clientIPAddress) {
		this.requestParameters = requestParameters;
		this.requestHeaders = requestHeaders;
		this.requestUrl = requestUrl;
		this.simbaWebURL = simbaWebURL;
		this.httpMethod = httpMethod;
		this.ssoToken = ssoToken;
		this.hostServerName = hostServerName;
		this.clientIsABrowser = clientIsABrowser;
		this.isLogoutRequest = isLogoutRequest;
		this.isLoginRequest = isLoginRequest;
	}

	public RequestData asRequestData() {
		String loginToken = null;
		boolean ssoTokenMappingKeyProvided = false;
		boolean changePasswordRequest = false;
		boolean showChangePasswordRequest = false;
		return new RequestData(requestParameters, requestHeaders, requestUrl, simbaWebURL, createSSOToken(), clientIPAddress,
				isLogoutRequest, isLoginRequest, ssoTokenMappingKeyProvided, changePasswordRequest, showChangePasswordRequest,
				httpMethod, hostServerName, loginToken);
	}

	public boolean isClientABrowser() {
		return clientIsABrowser;
	}

	private SSOToken createSSOToken() {
		return Strings.isNullOrEmpty(ssoToken) ? null : new SSOToken(ssoToken);
	}

	public Map<String, String> getRequestParameters() {
		return requestParameters;
	}

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public String getSimbaWebURL() {
		return simbaWebURL;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public String getHostServerName() {
		return hostServerName;
	}

	public boolean isLogoutRequest() {
		return isLogoutRequest;
	}

	public String getUserAgentHeader() {
		return getRequestHeaders() == null ? null : getRequestHeaders().get(HttpHeaders.USER_AGENT);
	}

	public String getSsoToken() {
		return ssoToken;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(hostServerName, httpMethod, requestHeaders, requestParameters, requestUrl, simbaWebURL, clientIsABrowser, clientIPAddress);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimbaCredentials other = (SimbaCredentials) obj;
		return Objects.equal(hostServerName, other.hostServerName)
				&& Objects.equal(httpMethod, other.httpMethod)
				&& Objects.equal(requestHeaders, other.requestHeaders)
				&& Objects.equal(requestParameters, other.requestParameters)
				&& Objects.equal(requestUrl, other.requestUrl)
				&& Objects.equal(simbaWebURL, other.simbaWebURL)
				&& Objects.equal(isLoginRequest, other.isLoginRequest)
				&& Objects.equal(isLogoutRequest, other.isLogoutRequest)
				&& Objects.equal(clientIsABrowser, other.clientIsABrowser)
				&& Objects.equal(clientIPAddress, other.clientIPAddress);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("requestParameters", requestParameters)
				.add("requestHeaders", requestHeaders)
				.add("requestUrl", requestUrl)
				.add("simbaWebURL", simbaWebURL)
				.add("httpMethod", httpMethod)
				.add("hostServerName", hostServerName)
				.add("clientIsABrowser", clientIsABrowser)
				.add("isLoginRequest", isLoginRequest)
				.add("isLogoutRequest", isLogoutRequest)
				.add("clientIPAddress", clientIPAddress)
				.toString();
	}
}
