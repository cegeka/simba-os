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

package org.simbasecurity.core.chain;

import org.simbasecurity.api.service.thrift.*;
import org.simbasecurity.core.chain.eid.SAMLUser;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.LoginMappingService;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.simbasecurity.common.constants.AuthenticationConstants.*;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;
import static org.simbasecurity.common.request.RequestUtil.addParametersToUrlAndFilterInternalParameters;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.*;
import static org.simbasecurity.core.exception.SimbaMessageKey.ACCESS_DENIED;
import static org.simbasecurity.core.exception.SimbaMessageKey.LOGIN_TIME_EXPIRED;

public class ChainContextImpl implements ChainContext {

    private static final String HTTP_SIMBA_CHANGE_PWD = "/http/simba-change-pwd";

    private static final String SIMBA_CREDENTIAL_CONTROLLER_PATH_SUFFIX = "http/simba-login";

    static final String USER_AGENT_HEADER = "user-agent";

    private static final long serialVersionUID = -4955438470368723192L;

    protected RequestData requestData;

    private ActionDescriptor actionDescriptor = new ActionDescriptor();

    private Session currentSession;

    private final ConfigurationServiceImpl configurationService;
    private final LoginMappingService loginMappingService;

    private long commandCounter = 0;
    private UUID uuiIdForAChain;
    private LoginMapping loginMapping;
    private SAMLUser samlUser;

    /**
     * Create a new ChainContext from the given {@link HttpServletRequest}. The
     * required info is stripped from the {@link HttpServletRequest} and stored
     * for easy access.
     * @param requestWrapper
     * @param session
     * @param configurationService
     */
    public ChainContextImpl(RequestData requestWrapper, Session session, ConfigurationServiceImpl configurationService, LoginMappingService loginMappingService) {
        this.requestData = requestWrapper;
        this.currentSession = session;
        this.configurationService = configurationService;
        this.loginMappingService = loginMappingService;
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setNewSession(Session session) {
        currentSession = session;
    }

    public String getRequestHeader(String name) {
        return requestData.getRequestHeaders().get(name);
    }

    public String getRequestParameter(String name) {
        return requestData.getRequestParameters().get(name);
    }

    public Map<String, String> getRequestParameters() {
        return requestData.getRequestParameters();
    }

    public String getRequestMethod() {
        return requestData.getRequestMethod();
    }

    public String getRequestURL() {
        return requestData.getRequestURL();
    }

    public SSOToken getRequestSSOToken() {
        return requestData.getSsoToken();
    }

    public void setSSOTokenForActions(SSOToken ssoToken) {
        getActionDescriptor().setSsoToken(ssoToken);
    }

    @Override
    public void setMappingTokenForActions(String mappingToken) {
        getActionDescriptor().setMappingToken(mappingToken);
    }

    public void setRedirectURL(String redirectURL) {
        getActionDescriptor().setRedirectURL(redirectURL);
    }

    public void addParameterToTarget(String key, String value) {
        getActionDescriptor().getParameterMap().put(key, value);
    }

    public void addParametersToTarget(Map<String,String> parameters) {
        Set<Entry<String, String>> entrySet = parameters.entrySet();
        for (Entry<String, String> parameter : entrySet) {
            getActionDescriptor().getParameterMap().put(parameter.getKey(), parameter.getValue());
        }
    }

    public void activateAction(ActionType actionType) {
        getActionDescriptor().getActionTypes().add(actionType);
    }

    public boolean isLogoutRequest() {
        return requestData.isLogoutRequest();
    }

    public String getClientIpAddress() {
        return requestData.getClientIPAddress();
    }

    public String getHostServerName() {
        return requestData.getHostServerName();
    }

    public boolean isLoginRequest() {
        return requestData.isLoginRequest();
    }

    public boolean isChangePasswordRequest() {
        return requestData.isChangePasswordRequest();
    }

    public boolean isShowChangePasswordRequest() {
        return requestData.isShowChangePasswordRequest();
    }

    public ActionDescriptor getActionDescriptor() {
        return actionDescriptor;
    }

    public String getSimbaWebURL() {
        return requestData.getSimbaWebURL();
    }

    public void setUserPrincipal(String userName) {
        actionDescriptor.setPrincipal(userName);
    }

    public boolean isSsoTokenMappingKeyProvided() {
        return requestData.isSsoTokenMappingKeyProvided();
    }

    public void redirectWithParameters(String redirectURL, Map<String, String> parameters) {
        activateAction(ActionType.ADD_PARAMETER_TO_TARGET);
        activateAction(ActionType.REDIRECT);
        addParametersToTarget(parameters);
        setRedirectURL(redirectURL);
    }

    public void redirectToChangePasswordWithFilter() {
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put(USERNAME, getUserName());
        addLoginTokenToRequestParameters(requestParameters, createLoginMapping());

        String url = getSimbaWebURL() + configurationService.getValue(CHANGE_PASSWORD_URL);
        redirectWithParameters(url, requestParameters);
    }

    public void setSAMLUser(SAMLUser samlUser) {
        this.samlUser = samlUser;
    }

    public SAMLUser getSAMLUser() {
        return this.samlUser;
    }

    public boolean isLoginUsingEID() {
        return getSAMLUser() != null;
    }

    private void addLoginTokenToRequestParameters(Map<String, String> requestParameters, LoginMapping loginMapping) {
        if (loginMapping != null) {
            requestParameters.put(LOGIN_TOKEN, loginMapping.getToken());
        }
    }

    @Override
    public LoginMapping createLoginMapping() {
        LoginMapping newMapping = null;
        if (getLoginToken() != null) {
            LoginMapping mapping = getLoginMapping() != null ? getLoginMapping() : loginMappingService.getMapping(getLoginToken());
            if (mapping != null) {
                newMapping = loginMappingService.createMapping(mapping.getTargetURL());
                loginMappingService.removeMapping(getLoginToken());
            }
        } else if(!isLoginUsingJSP()){
            String targetURL = addParametersToUrlAndFilterInternalParameters(getRequestURL(), getRequestParameters());
            newMapping = loginMappingService.createMapping(targetURL);
        }
        if (newMapping != null) {
            setLoginMapping(newMapping);
        }

        return getLoginMapping();
    }

    public void redirectToChangePasswordDirect(){
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put(USERNAME, getUserName());
        requestParameters.put(SIMBA_SSO_TOKEN, getRequestSSOToken().toString());

        LoginMapping loginToken = createLoginMapping();
        requestParameters.put(LOGIN_TOKEN, loginToken.getToken());

        String url = getSimbaWebURL() + configurationService.getValue(CHANGE_PASSWORD_URL);
        redirectWithParameters(url, requestParameters);
    }

    public void redirectToPasswordChanged(){
        String passwordChangedURL = configurationService.getValue(SimbaConfigurationParameter.PASSWORD_CHANGED_URL);
        redirectWithParameters(getSimbaWebURL() + passwordChangedURL, new HashMap<>());
    }

    public void redirectWhenLoginTokenExpired(){
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put(ERROR_MESSAGE, LOGIN_TIME_EXPIRED.name());
        redirectWithParameters(getSimbaWebURL() + configurationService.getValue(SimbaConfigurationParameter.EXPIRED_URL) , requestParameters);
    }

    public void redirectToLogin(){
        Map<String, String> parameters = new HashMap<>();

        LoginMapping loginToken = createLoginMapping();
        parameters.put(LOGIN_TOKEN, loginToken.getToken());
        String url = getSimbaWebURL() + configurationService.getValue(LOGIN_URL);
        redirectWithParameters(url, parameters);
    }

    public void redirectToLogout(){
        Map<String, String> parameters = new HashMap<>();
        String url = getSimbaWebURL() + configurationService.getValue(LOGOUT_URL);
        redirectWithParameters(url, parameters);
    }

    public void redirectToAccessDenied(){
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put(ERROR_MESSAGE, ACCESS_DENIED.name());
        String url = getSimbaWebURL() + configurationService.getValue(ACCESS_DENIED_URL);
        redirectWithParameters(url, requestParameters);
    }

    @Override
    public void redirectWithCredentialError(SimbaMessageKey errorKey) {
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put(USERNAME, getUserName());
        requestParameters.put(ERROR_MESSAGE, errorKey.name());

        addLoginTokenToRequestParameters(requestParameters, createLoginMapping());

        redirectWithParameters(getSimbaWebURL() + getCredentialPath(), requestParameters);
    }

    public boolean isLoginUsingJSP() {
        return getRequestURL().endsWith(SIMBA_CREDENTIAL_CONTROLLER_PATH_SUFFIX) || getRequestURL().endsWith(HTTP_SIMBA_CHANGE_PWD);
    }

    private String getCredentialPath() {
        if(isChangePasswordRequest()) {
            return configurationService.getValue(CHANGE_PASSWORD_URL);
        } else if(isLoginRequest()) {
            return configurationService.getValue(LOGIN_URL);
        } else {
            throw new IllegalStateException("Redirect with credential should be called from login or change password request");
        }
    }

    public String getUserName() {
        String userName = null;

        if (getSAMLUser() != null) {
            userName = getSAMLUser().getInsz();
        }

        if (isBlank(userName)) {
            userName = getRequestParameter(USERNAME);
        }

        if (isBlank(userName) && currentSession != null) {
            userName = currentSession.getUser().getUserName();
        }

        return userName;
    }

    @Override
    public String getChainContextId() {
        if(uuiIdForAChain == null){
            uuiIdForAChain = UUID.randomUUID();
        }
        return uuiIdForAChain.toString() + "-" + commandCounter;
    }

    @Override
    public void increaseCommandCounter() {
        commandCounter++;
    }

    @Override
    public void resetCommandCounter() {
        commandCounter = 0;
    }

    @Override
    public String getUserAgent() {
        return requestData.getRequestHeaders().get(USER_AGENT_HEADER);
    }

    @Override
    public String getLoginToken() {
        return requestData.getLoginToken();
    }

    @Override
    public LoginMapping getLoginMapping() {
        return loginMapping;
    }

    @Override
    public void setLoginMapping(LoginMapping loginMapping) {
        this.loginMapping = loginMapping;
    }

    @Override
    public String getSimbaEidSuccessUrl() {
        return requestData.getSimbaEidSuccessUrl();
    }
}
