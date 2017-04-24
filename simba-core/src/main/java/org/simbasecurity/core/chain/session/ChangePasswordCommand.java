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

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.simbasecurity.common.constants.AuthenticationConstants.NEW_PASSWORD;
import static org.simbasecurity.common.constants.AuthenticationConstants.NEW_PASSWORD_CONFIRMATION;
import static org.simbasecurity.core.audit.AuditMessages.PASSWORD_CHANGED;
import static org.simbasecurity.core.audit.AuditMessages.PASSWORD_NOT_VALID;

/**
 * The ChangePasswordCommand checks if a user is changing his password and
 * executes the required actions.
 *
 * @since 1.0
 */
@Component
public class ChangePasswordCommand implements Command {
    @Autowired private CredentialService credentialService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        if (context.isChangePasswordRequest()) {
            String userName = context.getUserName();
            String newPassword = context.getRequestParameter(NEW_PASSWORD);
            String newPasswordConfirmation = context.getRequestParameter(NEW_PASSWORD_CONFIRMATION);

            try {
                credentialService.changePassword(userName, newPassword, newPasswordConfirmation);
                audit.log(auditLogFactory.createEventForSessionForSuccess(context, PASSWORD_CHANGED));
                if (isChangePasswordDuringSession(context)) {
                    context.redirectToPasswordChanged();
                    return State.FINISH;
                }
            } catch (SimbaException e) {
                audit.log(auditLogFactory.createEventForSessionForFailure(context, PASSWORD_NOT_VALID));
                context.redirectWithCredentialError(e.getMessageKey());
                return State.FINISH;
            }
        }
        return State.CONTINUE;
    }

    private boolean isChangePasswordDuringSession(ChainContext context) {
        return context.getCurrentSession() != null;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

}
