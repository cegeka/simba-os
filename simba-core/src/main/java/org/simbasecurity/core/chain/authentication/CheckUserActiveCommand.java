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
package org.simbasecurity.core.chain.authentication;

import static org.simbasecurity.core.audit.AuditMessages.*;
import static org.simbasecurity.core.exception.SimbaMessageKey.*;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The CheckUserActiveCommand checks if a user exists in the data store and if
 * the user's account is not inactive. If it doesn't exist or is inactive, the
 * user will receive a login failed message.
 *
 * @since 1.0
 */
@Component
public class CheckUserActiveCommand implements Command {

    @Autowired private CredentialService credentialService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        if (!credentialService.checkUserExists(context.getUserName())
                || credentialService.checkUserStatus(context.getUserName(), Status.INACTIVE)) {
        	logFailure(context,ACCOUNT_NOT_EXISTS_OR_INACTIVE);
            context.redirectWithCredentialError(LOGIN_FAILED);
            return State.FINISH;
        }

        context.setUserPrincipal(context.getUserName());
        logSuccess(context, CHECK_USER_ACTIVE);
        return State.CONTINUE;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

    @Override
    public void logSuccess(ChainContext context, String message) {
    	audit.log(auditLogFactory.createEventForAuthenticationForSuccess(context, message));
    }

    @Override
    public void logFailure(ChainContext context, String message) {
    	audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, message));
    }

}
