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
import org.simbasecurity.core.domain.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.simbasecurity.core.audit.AuditMessages.CLIENT_IP_CHECK;

/**
 * The CheckClientIPCommand checks if the client IP in the request is the same
 * as the one stored for this session. If this is not the case, the user is
 * redirected to the login page.
 *
 * @since 1.0
 */
@Component
public class CheckClientIPCommand implements Command {

    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        boolean isTheSameIP = isAccessibleFrom(context.getCurrentSession(), context.getClientIpAddress());

        if (!isTheSameIP) {
            audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, AuditMessages.IP_IN_SESSION_NOT_THE_SAME));
            context.redirectToLogin();
            return State.FINISH;
        }
        audit.log(auditLogFactory.createEventForAuthenticationForSuccess(context, AuditMessages.CHECK_CLIENT_IP));
        return State.CONTINUE;
    }

    /**
     * Check if a given IP address matches the one stored in this session.
     *
     * @param ipAddress an IP address to check.
     * @return <tt>true</tt> if the IP addresses match; <tt>false</tt> otherwise
     */
    public boolean isAccessibleFrom(Session session, String ipAddress) {
        boolean ok = true;
        if (!session.getClientIpAddress().equals(ipAddress)) {
            audit.log(auditLogFactory.createEventForSession(session.getUser().getUserName(),
                                                           session.getSSOToken(),
                                                           ipAddress, CLIENT_IP_CHECK + session.getClientIpAddress()));
            ok = false;
        }
        return ok;
    }


    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

}
