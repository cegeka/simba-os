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

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The CheckSession command checks if a valid HTTP Session is available. If the
 * Session isn't there, the user is redirected to a login page.
 *
 * @since 1.0
 */
@Component
public class CheckSessionCommand implements Command {

    @Autowired private SessionService sessionService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        SSOToken ssoToken;
        if (context.isSsoTokenMappingKeyProvided() && context.getCurrentSession() != null) {
            ssoToken = context.getCurrentSession().getSSOToken();
        } else {
            ssoToken = context.getRequestSSOToken();
        }
        if (ssoToken == null) {
            redirectToLogin(context);

            audit.log(auditLogFactory.createEventForAuthentication(context, AuditMessages.NO_SSOTOKEN_FOUND_REDIRECT_LOGIN));
            return State.FINISH;
        }

        Session currentSession = context.getCurrentSession();

        if (currentSession == null || currentSession.isExpired()) {
            redirectToLogin(context);
            sessionService.removeSession(currentSession);
            audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, AuditMessages.SESSION_INVALID));
            return State.FINISH;
        }

        currentSession.updateLastAccesTime();
        context.setUserPrincipal(currentSession.getUser().getUserName());
        audit.log(auditLogFactory.createEventForAuthenticationForSuccess(context, AuditMessages.CHECK_SESSION));
        return State.CONTINUE;
    }

    protected void redirectToLogin(ChainContext context) {
        context.redirectToLogin();
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

}
