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
package org.simbasecurity.common.constants;

import org.simbasecurity.common.request.RequestConstants;

import java.util.Arrays;
import java.util.Collection;

public interface AuthenticationConstants extends RequestConstants {
    /**
     * The parameter name used in the FORM for retrieving the user name.
     */
    String USERNAME = "username";

    /**
     * The parameter name used in the FORM for retrieving the password.
     */
    String PASSWORD = "password";

    String NEW_PASSWORD = "newpassword";

    String NEW_PASSWORD_CONFIRMATION = "newpasswordconfirmation";

    /**
     * The parameter name used in the FORM for retrieving the target URL
     */
    String TARGET_URL = "targetURL";

    /**
     * The key used to store the subject into the session.
     */
    String SESSION_SUBJECT = "simbaSessionSubject";

    String AUTHENTICATION_VALID = "authenticationValid";

    String ERROR_MESSAGE = "errorMessage";
    
    /**
	 * The parameter name that contains the temporary token for login to determine the original target URL. 
	 */
	String LOGIN_TOKEN = "loginToken";
	
	Collection<String> SIMBA_INTERNALS_REQUEST_CONSTANTS = Arrays.asList(USERNAME, PASSWORD, LOGIN_TOKEN, SIMBA_ACTION_PARAMETER, SIMBA_SSO_TOKEN, SAML_RESPONSE);
	
}
