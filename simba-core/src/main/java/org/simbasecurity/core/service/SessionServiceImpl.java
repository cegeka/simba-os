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
package org.simbasecurity.core.service;

import static org.simbasecurity.core.audit.AuditMessages.*;

import java.util.Collection;
import java.util.UUID;

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.SessionEntity;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.SessionRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SessionServiceImpl implements SessionService {

    @Autowired private Audit audit;

    @Autowired private UserRepository userRepository;
    @Autowired private SessionRepository sessionRepository;

    @Qualifier("ArchiveSessionService")
    @Autowired
    private ArchiveSessionService archiveSessionService;
    @Autowired private AuditLogEventFactory auditLogEventFactory;

    @Override
    public Session createSession(String userName, String clientIpAddress, String hostServerName, String userAgent,String requestURL) {
        User user = userRepository.findByName(userName);

        SSOToken ssoToken = new SSOToken(UUID.randomUUID().toString());
        Session session = new SessionEntity(user, ssoToken, clientIpAddress, hostServerName);

        sessionRepository.persist(session);
        audit.log(auditLogEventFactory.createEventForSession(user.getUserName(), ssoToken, clientIpAddress, hostServerName, userAgent, requestURL, SESSION_CREATED));
        return session;
    }

    @Override
    public void removeSession(Session session) {
        archiveSession(session);
    	sessionRepository.remove(session);
    }


	@Override
    public Session getSession(SSOToken token) {
        if (token == null)
            return null;
        return sessionRepository.findBySSOToken(token);
    }

    @Override
    public void purgeExpiredSessions() {
        Collection<Session> sessions = sessionRepository.findAll();

        for (Session session : sessions) {
            if (session.isExpired()) {
            	archiveSession(session);
            	audit.log(auditLogEventFactory.createEventForSession(session.getUser().getUserName(), session.getSSOToken(), session.getClientIpAddress(), "Purged expired session"));
                sessionRepository.remove(session);
            }
        }
    }
    private void archiveSession(Session session) {
    	 archiveSessionService.archive(session);    	
    }
}
