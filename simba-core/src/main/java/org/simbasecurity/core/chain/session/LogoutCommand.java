/*
 * Copyright 2011 Simba Open Source
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
package org.simbasecurity.core.chain.session;

import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The LogoutCommand checks if the user has sent a logout request. If this is
 * the case, the user's session and SSO cookie are deleted, and the user is
 * redirected to the logout page.
 *
 * @since 1.0
 */
@Component
public class LogoutCommand implements Command {

    @Autowired private SessionService sessionService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        if (context.isLogoutRequest()) {
            sessionService.removeSession(context.getCurrentSession());

            audit.log(auditLogFactory.createEventForSessionForSuccess(context, AuditMessages.LOGGED_OUT + ": SSOToken=" + context.getRequestSSOToken()));

            context.activateAction(ActionType.DELETE_COOKIE);
            redirectToLogout(context);
            return State.FINISH;
        }
        return State.CONTINUE;
    }

    protected void redirectToLogout(ChainContext context) {
        context.redirectToLogout();
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }
}
