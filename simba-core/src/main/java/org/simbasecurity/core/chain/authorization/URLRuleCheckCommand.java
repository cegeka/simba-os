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
package org.simbasecurity.core.chain.authorization;

import static org.simbasecurity.core.audit.AuditMessages.*;

import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The URLRuleCheckCommand checks if a user is allowed to access a specific URL.
 * If this is not the case, the user is redirected to an access denied page.
 *
 * @since 1.0
 */
@Component
public class URLRuleCheckCommand implements Command {

    @Autowired private AuthorizationService.Iface authorizationService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        if (this.authorizationService.isURLRuleAllowed(context.getUserName(), context.getRequestURL(),
                context.getRequestMethod()).isAllowed()) {
        	logSuccess(context, AuditMessages.CHECK_URL_RULE);
            return State.CONTINUE;
        }

        logFailure(context, ACCESS_DENIED + context.getRequestURL());
        context.redirectToAccessDenied();

        return State.FINISH;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

    @Override
    public void logSuccess(ChainContext context, String message) {
    	audit.log(auditLogFactory.createEventForAuthorizationForSuccess(context, message));
    }

    @Override
    public void logFailure(ChainContext context, String message) {
    	audit.log(auditLogFactory.createEventForAuthorizationForFailure(context, message));
    }
}
