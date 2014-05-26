/*
 * Copyright 2013 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.simbasecurity.common.request;

import static org.simbasecurity.common.constants.AuthenticationConstants.LOGIN_TOKEN;
import static org.simbasecurity.common.request.RequestConstants.HEADER_X_FORWARDED_FOR;
import static org.simbasecurity.common.request.RequestConstants.HEADER_X_ORIGINAL_SCHEME;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_ACTION_PARAMETER;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_CHANGE_PASSWORD_ACTION;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_LOGIN_ACTION;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_LOGIN_PATH;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_LOGOUT_ACTION;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_SHOW_CHANGE_PASSWORD_ACTION;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.common.util.StringUtil;
import org.simbasecurity.common.xpath.UserNameTokenNamespaceContext;
import org.w3c.dom.Element;

public final class RequestUtil {

	private static final String HTTPS = "https";

	private static final String XPATH_PASSWORD = "./wsse:Security/wsse:UsernameToken/wsse:Password/text()";
	private static final String XPATH_USERNAME = "./wsse:Security/wsse:UsernameToken/wsse:Username/text()";

	private static final XPath XPATH = XPathFactory.newInstance().newXPath();

	static {
		XPATH.setNamespaceContext(new UserNameTokenNamespaceContext());
	}

	public static final String HOST_SERVER_NAME;

	private RequestUtil() {
	}

	static {
		String hostName;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ignored) {
			hostName = "<UNKNOWN>";
		}
		HOST_SERVER_NAME = hostName;
	}

	public static String buildURL(final String url, final String parameterName, final String parameterValue) {
		return buildURL(url, Collections.singletonMap(parameterName, parameterValue));
	}

	public static String buildURL(final String url, final Map<String, String> parameters) {
		final StringBuilder stringBuilder = new StringBuilder(url);

		boolean hasQuestionMark = url.contains("?");

		for (final Entry<String, String> entry : parameters.entrySet()) {
			if (hasQuestionMark) {
				stringBuilder.append('&');
			} else {
				stringBuilder.append('?');
				hasQuestionMark = true;
			}

			stringBuilder.append(entry.getKey()).append('=').append(entry.getValue());
		}

		return stringBuilder.toString();
	}

	public static RequestData createRequestData(final HttpServletRequest request, final String simbaWebURL) {
		return new RequestData(retrieveRequestParameters(request), retrieveRequestHeaders(request), resolveProtocol(request), simbaWebURL,
				getSsoToken(request), retrieveClientIpAddress(request), isLogoutRequest(request), isLoginRequest(request),
				isSSOTokenMappingKeyProvided(request), isChangePasswordRequest(request), isShowChangePasswordRequest(request), request.getMethod(),
				HOST_SERVER_NAME, getLoginToken(request));
	}

	public static RequestData createWSSERequestData(final HttpServletRequest request, final Element wsseHeader, final String simbaWebURL) {
		final Map<String, String> requestParameters = retrieveRequestParameters(request);

		try {
			requestParameters.put(AuthenticationConstants.USERNAME, XPATH.evaluate(XPATH_USERNAME, wsseHeader));
			requestParameters.put(AuthenticationConstants.PASSWORD, XPATH.evaluate(XPATH_PASSWORD, wsseHeader));
		} catch (XPathExpressionException ignore) {
		}

		return new RequestData(requestParameters, retrieveRequestHeaders(request), resolveProtocol(request), simbaWebURL, null,
				retrieveClientIpAddress(request), false, true, false, false, false, request.getMethod(), HOST_SERVER_NAME, getLoginToken(request));
	}

	private static String resolveProtocol(final HttpServletRequest request) {
		final String header = request.getHeader(HEADER_X_ORIGINAL_SCHEME);
		final String requestURL = request.getRequestURL().toString();

		if (isNotBlank(header) && header.equals(HTTPS)) {
			return convertToHTTPS(requestURL);
		} else {
			return requestURL;
		}
	}

	private static String convertToHTTPS(final String url) {
		try {
			final String urlWithoutProtocol = StringUtil.substringAfter(url, new URL(url).getProtocol());
			return HTTPS + urlWithoutProtocol;

		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getLoginToken(HttpServletRequest request) {
		String loginToken = request.getParameter(LOGIN_TOKEN);
		return loginToken;
	}

	private static boolean isLoginRequest(final HttpServletRequest request) {
		return SIMBA_LOGIN_ACTION.equals(request.getParameter(SIMBA_ACTION_PARAMETER)) || SIMBA_LOGIN_PATH.equals(request.getPathInfo());
	}

	private static boolean isLogoutRequest(final HttpServletRequest request) {
		return SIMBA_LOGOUT_ACTION.equals(request.getParameter(SIMBA_ACTION_PARAMETER));
	}

	private static boolean isChangePasswordRequest(final HttpServletRequest request) {
		return SIMBA_CHANGE_PASSWORD_ACTION.equals(request.getParameter(SIMBA_ACTION_PARAMETER));
	}

	private static boolean isShowChangePasswordRequest(final HttpServletRequest request) {
		return SIMBA_SHOW_CHANGE_PASSWORD_ACTION.equals(request.getParameter(SIMBA_ACTION_PARAMETER));
	}

	private static boolean isSSOTokenMappingKeyProvided(final HttpServletRequest request) {
		return isNotBlank(getSSOTokenMappingKey(request));
	}

	private static String getSSOTokenMappingKey(final HttpServletRequest request) {
		return request.getParameter(SIMBA_SSO_TOKEN);
	}

	private static boolean isNotBlank(final String string) {
		return string != null && string.trim().length() > 0;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, String> retrieveRequestHeaders(final HttpServletRequest request) {
		final Map<String, String> headers = new HashMap<String, String>();

		final Enumeration<String> names = request.getHeaderNames();

		while (names.hasMoreElements()) {
			final String name = names.nextElement();
			headers.put(name, request.getHeader(name));
		}
		return headers;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, String> retrieveRequestParameters(final HttpServletRequest request) {
		final Map<String, String> parameters = new HashMap<String, String>();

		final Enumeration<String> names = request.getParameterNames();

		while (names.hasMoreElements()) {
			final String name = names.nextElement();
			parameters.put(name, request.getParameter(name));
		}
		return parameters;
	}

	/**
	 * First check the cookie, fallback to request header
	 */
	public static SSOToken getSsoToken(final HttpServletRequest request) {
		final Cookie ssoCookie = getSSOCookie(request);
		if (ssoCookie != null) {
			return new SSOToken(ssoCookie.getValue());
		}

		if (request.getHeader("simbaSSOToken") != null) {
			return new SSOToken(request.getHeader("simbaSSOToken"));
		}

		return null;
	}

	/**
	 * Let is explode if nothing found
	 */
	public static SSOToken getSsoTokenThatShouldBePresent(final HttpServletRequest request) {
		SSOToken token = getSsoToken(request);
		if (token == null) {
			throw new IllegalStateException("no SSOToken found");
		}
		return token;
	}

	private static Cookie getSSOCookie(final HttpServletRequest request) {
		final Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (final Cookie cookie : cookies) {
				if (RequestConstants.SIMBA_SSO_TOKEN.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

	public static Cookie getSSOCookieThatShouldBePresent(final HttpServletRequest request) {
		Cookie ssoCookie = getSSOCookie(request);
		if (ssoCookie == null) {
			throw new IllegalStateException("no Cookie found");
		}
		return ssoCookie;
	}

	public static String retrieveClientIpAddress(final HttpServletRequest request) {
		String remoteAddress = request.getRemoteAddr();

		try {
			final InetAddress inetAddress = InetAddress.getByName(remoteAddress);
			if (inetAddress.isLoopbackAddress()) {
				remoteAddress = InetAddress.getLocalHost().getHostAddress();
			}
		} catch (UnknownHostException ignored) {
		}

		final String xForwardedFor = request.getHeader(HEADER_X_FORWARDED_FOR);

		if (xForwardedFor != null) {
			final int idx = xForwardedFor.indexOf(',');
			if (idx >= 0) {
				remoteAddress = xForwardedFor.substring(0, idx).trim();
			} else {
				remoteAddress = xForwardedFor.trim();
			}
		}
		return remoteAddress;
	}
}
