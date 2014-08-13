package org.simbasecurity.dwclient.dropwizard.credentials;

import static com.yammer.dropwizard.assets.ResourceURL.*;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpMethods;
import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.common.request.RequestUtil;
import org.simbasecurity.dwclient.exception.IllegalArgumentDWSimbaException;

import com.google.common.collect.Maps;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;

public class SimbaCredentialsFactory {

	private String simbaWebURL;

	@Inject
	public SimbaCredentialsFactory(@Named("simbaWebURL") String simbaWebURL) {
		this.simbaWebURL = simbaWebURL;
	}

	public SimbaCredentials create(ContainerRequest containerRequest) {
		return create(containerRequest, false);
	}

	public SimbaCredentials createForLogin(String username, String password) {
		Map<String, String> requestParameters = Maps.newHashMap();
		addUsernameAndPasswordToRequestParams(username, password, requestParameters);

		Map<String, String> requestHeaders = Maps.newHashMap();
		String emptyBecauseWeDontCareButSimbaDoes = "";
		requestHeaders.put("user-agent", emptyBecauseWeDontCareButSimbaDoes);

		return new SimbaCredentials(requestParameters, requestHeaders, emptyBecauseWeDontCareButSimbaDoes, simbaWebURL,
				HttpMethods.POST, null, RequestUtil.HOST_SERVER_NAME, false, true, false, null);
	}

	private void addUsernameAndPasswordToRequestParams(String username, String password, Map<String, String> requestParameters) {
		requestParameters.put(AuthenticationConstants.USERNAME, username);
		requestParameters.put(AuthenticationConstants.PASSWORD, password);
	}

	public SimbaCredentials create(ContainerRequest containerRequest, boolean isLogoutRequest) {
		Map<String, String> requestParameters = toMap(containerRequest.getQueryParameters());
		String auth = containerRequest.getHeaderValue(HttpHeaders.AUTHORIZATION);
		String ssotoken = containerRequest.getHeaderValue(org.simbasecurity.dwclient.dropwizard.http.HttpHeaders.X_SSO_TOKEN);
		boolean isLoginRequest = false;
		if (ssotoken == null && auth == null) {
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
					.entity("Neither SSOToken, nor authorization header found on request")
					.type(MediaType.APPLICATION_JSON)
					.build());
		}
		if (auth != null) {
			String[] lap = decode(auth);
			addUsernameAndPasswordToRequestParams(lap[0], lap[1], requestParameters);
			isLoginRequest = true;
		}
		Map<String, String> requestHeaders = toMap(containerRequest.getRequestHeaders());

		try {
			return new SimbaCredentials(requestParameters,
					requestHeaders,
					appendTrailingSlash(containerRequest.getAbsolutePath().toURL()).toString(),
					simbaWebURL,
					containerRequest.getMethod(),
					ssotoken, RequestUtil.HOST_SERVER_NAME,
					false,
					isLoginRequest,
					isLogoutRequest,
					null);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentDWSimbaException(e.getMessage());
		}
	}

	private String[] decode(String auth) {
		if (auth.toLowerCase().startsWith("basic ")) {
			String[] usernamePassword = decodeBasic(auth);
			if (usernamePassword.length != 2) {
				throw new WebApplicationException(401);
			}
			return usernamePassword;
		}
		throw new UnsupportedOperationException("Only Basic Authentication supported so far");
	}

	private String[] decodeBasic(String auth) {
		String digest = auth.substring(6);
		String decodedString = Base64.base64Decode(digest);
		return decodedString != null ? decodedString.split(":", 2) : null;
	}

	private <K, V> Map<K, V> toMap(Map<K, List<V>> map) {
		Map<K, V> result = new HashMap<K, V>();
		for (Map.Entry<K, List<V>> entry : map.entrySet()) {
			List<V> list = entry.getValue();
			if (list.size() > 0) {
				result.put(entry.getKey(), list.get(0));
			}
		}
		return result;
	}
}
