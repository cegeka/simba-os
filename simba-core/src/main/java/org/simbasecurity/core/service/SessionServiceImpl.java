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
package org.simbasecurity.core.service;

import org.apache.thrift.TException;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.api.service.thrift.TSession;
import org.simbasecurity.api.service.thrift.TUser;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.ManagementAudit;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.SessionEntity;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.SessionRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.service.thrift.ThriftAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.simbasecurity.core.audit.AuditMessages.SESSION_CREATED;

@Transactional
@Service("sessionService")
public class SessionServiceImpl implements SessionService, org.simbasecurity.api.service.thrift.SessionService.Iface {

	@Autowired private Audit audit;

	@Autowired private UserRepository userRepository;
	@Autowired private SessionRepository sessionRepository;

	@Qualifier("ArchiveSessionService")
	@Autowired private ArchiveSessionService archiveSessionService;
	@Autowired private AuditLogEventFactory auditLogEventFactory;

	@Autowired private ThriftAssembler assembler;

	@Autowired private ManagementAudit managementAudit;

	@Override
	public Session createSession(String userName, String clientIpAddress, String hostServerName, String userAgent, String requestURL) {
		User user = userRepository.findByName(userName);

		SSOToken ssoToken = new SSOToken(UUID.randomUUID().toString());
		Session session = new SessionEntity(user, ssoToken, clientIpAddress, hostServerName);

		sessionRepository.persist(session);
		audit.log(auditLogEventFactory.createEventForSession(user.getUserName(), ssoToken, clientIpAddress, hostServerName, userAgent, requestURL,
				SESSION_CREATED));
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
				audit.log(auditLogEventFactory.createEventForSession(session.getUser().getUserName(), session.getSSOToken(),
						session.getClientIpAddress(), "Purged expired session"));
				sessionRepository.remove(session);
			}
		}
	}

	@Override
	public void purgeSessionsOlderThanAbsoluteSessionTimeOut() {
		// TODO philipn finish
	}

	private void archiveSession(Session session) {
		archiveSessionService.archive(session);
	}

	@Override
	public List<TSession> findAllActive() throws TException {
		return assembler.list(sessionRepository.findAllActive());
	}

	@Override
	public void remove(String ssoToken) throws TException {
		removeSession(getSession(new SSOToken(ssoToken)));

		managementAudit.log("Session with token ''{0}'' removed.", ssoToken);

	}

	@Override
	public void removeAllBut(String ssoToken) throws TException {
		sessionRepository.removeAllBut(new SSOToken(ssoToken));

        managementAudit.log("Removed all sessions");
    }

	@Override
	public TUser getUserFor(String ssoToken) throws TException {
		User user = getSession(new SSOToken(ssoToken)).getUser();
		return assembler.assemble(user);
	}
}
