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
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The CheckShowChangePasswordCommand checks if a user requests to change his
 * password and redirects him to the change password page.
 *
 * @since 1.0
 */
@Component
public class CheckShowChangePasswordCommand implements Command {

    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        if (context.isShowChangePasswordRequest()) {
        	audit.log(auditLogFactory.createEventForAuthentication(context, AuditMessages.REDIRECT_TO_CHANGE_PASSWORD));
            context.redirectToChangePasswordDirect();
            return State.FINISH;
        }
        audit.log(auditLogFactory.createEventForAuthenticationForSuccess(context, AuditMessages.CHECK_SHOW_PASSWORD));
        return State.CONTINUE;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

}
