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
package org.simbasecurity.core.chain.session;

import org.apache.commons.lang.StringUtils;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.common.request.RequestConstants;
import org.simbasecurity.common.request.RequestUtil;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.domain.SSOTokenMapping;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.SSOTokenMappingService;
import org.simbasecurity.core.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Map.Entry;

/**
 * The CreateSessionCommand creates a session for a user if there's none
 * available.
 *
 * @since 1.0
 */
@Component
public class CreateSessionCommand implements Command {

    @Autowired
    private SessionService sessionService;
    @Autowired
    private CredentialService credentialService;
    @Autowired
    private Audit audit;
    @Autowired
    private SSOTokenMappingService ssoTokenMappingService;
    @Autowired
    private AuditLogEventFactory auditLogFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        String targetURL;

        if (context.isLoginUsingJSP()) {
            LoginMapping mapping = context.getLoginMapping();
            if (mapping != null) {
                targetURL = mapping.getTargetURL();
            } else {

                String successURL = credentialService.getSuccessURL(context.getUserName());
                if (StringUtils.isBlank(successURL)) {
                    audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, AuditMessages.EMPTY_SUCCESS_URL));
                    context.redirectWithCredentialError(SimbaMessageKey.EMPTY_SUCCESS_URL);
                    return State.FINISH;
                }

                targetURL = successURL;
            }
        } else if (context.isLoginUsingEID()) {
            targetURL = context.getSimbaEidSuccessUrl();
        } else {
            targetURL = context.getRequestURL();
        }

        Session session = sessionService.createSession(context.getUserName(), context.getClientIpAddress(), context
                .getHostServerName(), context.getUserAgent(), context.getRequestURL());
        SSOTokenMapping ssoMappingToken = ssoTokenMappingService.createMapping(session.getSSOToken());
        targetURL = RequestUtil.addParametersToUrlAndFilterInternalParameters(targetURL, context.getRequestParameters());

        if (!context.isLoginUsingJSP()) {
            context.activateAction(ActionType.MAKE_COOKIE);
            context.setSSOTokenForActions(session.getSSOToken());
            context.setMappingTokenForActions(ssoMappingToken.getToken());
        } else {
            targetURL = RequestUtil.addParameterToUrl(targetURL, RequestConstants.SIMBA_SSO_TOKEN, ssoMappingToken.getToken());
        }

        context.activateAction(ActionType.REDIRECT);
        context.setRedirectURL(targetURL);
        context.setNewSession(session);

        audit.log(auditLogFactory.createEventForSessionForSuccess(context, AuditMessages.SESSION_CREATED + ": SSOToken=" + session.getSSOToken().getToken()));
        return State.FINISH;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

}