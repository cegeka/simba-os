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
package org.simbasecurity.common.request;

/**
 * A collection of constants used for request processing in Simba.
 */
public interface RequestConstants {

    /**
     * The header parameter name for X-Forwarded-For.
     */
    String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";

    /**
     * The header parameter name the original requests' protocol.
     */
    String HEADER_X_ORIGINAL_SCHEME = "X-ORIGINAL-SCHEME";

    /**
     * The parameter name used in the request to identify the SSO session.
     */
    String SIMBA_SSO_TOKEN = "simbaSSOToken";

    /**
     * The request parameter key defining the action to be taken.
     */
    String SIMBA_ACTION_PARAMETER = "SimbaAction";

    /**
     * The action as parameter in a request to define the request as a logout request.
     */
    String SIMBA_LOGOUT_ACTION = "SimbaLogoutAction";

    /**
     * The action as parameter in a request to define the request as a login request.
     */
    String SIMBA_LOGIN_ACTION = "SimbaLoginAction";

    /**
     * The action as parameter in a request to define the request as a password change request.
     */
    String SIMBA_CHANGE_PASSWORD_ACTION = "SimbaChangePasswordAction";

    /**
     * The action as parameter in a request to indicate that change password screen must be shown.
     */
    String SIMBA_SHOW_CHANGE_PASSWORD_ACTION = "SimbaShowChangePasswordAction";

    /**
     * The path info used for direct login on simba.
     */
    String SIMBA_LOGIN_PATH = "/simba-login";

    /**
     * The SAML response when using redirect binding
     */
    String SAML_RESPONSE = "SAMLResponse";

}
