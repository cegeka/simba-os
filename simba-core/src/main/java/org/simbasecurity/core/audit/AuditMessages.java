/*
 * Copyright 2013-2017 Simba Open Source
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
 *
 */
package org.simbasecurity.core.audit;

public interface AuditMessages {
	String MUST_CHANGE_PASSWORD = "Must change password";
	String JAAS_LOGIN_FAILED = "JAAS Login failed";
	String LOGGED_OUT = "Logged out";

	String SESSION_CREATED = "Session created";
	String CLIENT_IP_CHECK = "Session accessed from wrong remote address. Expected ";
	String IP_IN_SESSION_NOT_THE_SAME = "IP of the request is not the same as the IP in the current session for this user.  Possible cookie Hijacking";
	String PASSWORD_CHANGED = "Password changed";
	String PASSWORD_NOT_VALID = "Password not valid";
	String ACCESS_DENIED = "Access denied to ";
	String ACCOUNT_BLOCKED = "Account blocked";
	String ACCOUNT_NOT_EXISTS_OR_INACTIVE = "Account does not exist or is inactive";
	String DENIED_ACCESS_TO_BLOCKED_ACCOUNT = "User access denied; account is blocked";
	String EMPTY_USERNAME = "Username was empty";
	String EMPTY_PASSWORD = "PASSWORD was empty";
	String SESSION_INVALID = "Session was invalid";
	String NO_SSOTOKEN_FOUND_REDIRECT_LOGIN = "No SSOToken found, redirecting to login";
	String EMPTY_SUCCESS_URL = "No success URL specified for this user";
	String NO_FUNCTIONAL_AUDIT_LOGGIN = "No functional audit logging possible";
	String INVALID_HTTP_REQUEST_METHOD = "invalid http request method";
	String INVALID_SAML_RESPONSE = "Invalid SAML response";

	String SUCCESS = "SUCCESS: ";
	String FAILURE = "FAILURE: ";

	String CHAIN_SUCCESS = "Chain finish successfull at ";
	String CHAIN_FAILURE = "Chain failed at ";

	String WRONG_PASSWORD = "Incorrect password.";
	
	// authorization
	String USER_ALLOWED_IN_ROLE_LABEL = "User allowed in Role ";
	String USER_HAS_ADMIN_ROLE = "User has admin Role ";
	String URL_RESOURCE_LABEL = "URL Resource ";
	String LOG_DELIM = " - ";
	String RESOURCE_LABEL = "Resource ";
	String FROM_CACHE = " from cache ";

	// success commands
	String CHECK_ACCOUNT_BLOCKED = "check account blocked";
	String CHECK_PASSWORD_EXPIRED = "check password expired";
	String CHECK_USER_ACTIVE = "check user active";
	String NO_EXCLUDED_RESOURCE = "The resource is not excluded: ";
	String JAAS_LOGIN_SUCCESS = "JAAS Login succeeded";
	String VALID_REQUEST_PARAM = "Valid request parameters";
	String CHECK_URL_RULE = "url rule valid";
	String CHECK_CLIENT_IP = "check client ip";
	String CHECK_SESSION = "check if there is still a session";
	String REDIRECT_TO_CHANGE_PASSWORD = "Redirect to the change password screen";
	String CHECK_SHOW_PASSWORD = "check to show change password screen";
	String ENTER_APPLICATION = "entered the application";
	String CHECK_HTTP_REQUEST_METHOD = "http request method allowed";
	String VALID_SAML_RESPONSE = "Valid SAML response";

}
