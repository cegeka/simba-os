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
package org.simbasecurity.core.chain;

import java.io.Serializable;
import java.util.Map;

import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.chain.eid.SAMLUser;
import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.exception.SimbaMessageKey;

/**
 * A ChainContext represents the state information that is accessed and manipulated by the execution
 * of a {@link org.simbasecurity.core.chain.Command} or a {@link org.simbasecurity.core.chain.Chain}.
 *
 * @since 1.0
 */
public interface ChainContext extends Serializable {

    Session getCurrentSession();

    void setNewSession(Session session);

    /**
     * @param name the name for the request header to retrieve
     * @return the request header; or <tt>null</tt> if there is no header with the specified name
     */
    String getRequestHeader(String name);

    /**
     * @param name the name for the request parameter to retrieve
     * @return the request parameter; or <tt>null</tt> if there is no parameter with the specified name
     */
    String getRequestParameter(String name);

    /**
     * @returns all existings params of the url to avoid that business request params such als ?language=fr get stripped
     */
     Map<String, String> getRequestParameters();

    /**
     * @return the name of the HTTP method with which this request was made. This is one of <code>GET</code>,
     *         <code>POST</code> or <code>PUT</code>. <p/><em>Note that HTTP PUT methods are not yet supported by Simba</em>.
     */
    String getRequestMethod();

    String getRequestURL();

    SSOToken getRequestSSOToken();

    String getHostServerName();

    void setSSOTokenForActions(SSOToken requestSSOToken);

    void setRedirectURL(String redirectURL);

    void addParameterToTarget(String key, String value);

    void activateAction(ActionType redirect);

    String getClientIpAddress();

    boolean isLoginRequest();

    boolean isLogoutRequest();

    boolean isChangePasswordRequest();

    String getSimbaWebURL();

    String getUserName();

    void setUserPrincipal(String userName);

    boolean isSsoTokenMappingKeyProvided();

    void redirectWithCredentialError(SimbaMessageKey errorKey);

    ActionDescriptor getActionDescriptor();

    void redirectToPasswordChanged();

    void redirectToLogin();

    void redirectToLogout();

    void redirectToAccessDenied();

    LoginMapping createLoginMapping();

    void redirectToChangePasswordDirect();

    boolean isShowChangePasswordRequest();
    
	String getChainContextId();

	void increaseCommandCounter();
	
	void resetCommandCounter();
	
	String getUserAgent();
	
	String getLoginToken();
	
	boolean isLoginUsingJSP();

	void setLoginMapping(LoginMapping loginMapping);
	
	LoginMapping getLoginMapping();
	
	void redirectWhenLoginTokenExpired();

    void redirectWithParameters(String redirectURL, Map<String, String> parameters);

    void redirectToChangePasswordWithFilter();

    void setSAMLUser(SAMLUser samlUser);
}
