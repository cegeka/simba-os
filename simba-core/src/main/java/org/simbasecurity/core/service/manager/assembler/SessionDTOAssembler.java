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
package org.simbasecurity.core.service.manager.assembler;

import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.service.manager.dto.SessionDTO;

import java.util.ArrayList;
import java.util.Collection;

public final class SessionDTOAssembler {
    private SessionDTOAssembler() {
    }

    public static Collection<SessionDTO> assemble(final Collection<Session> sessions) {
        final Collection<SessionDTO> sessionDTOs = new ArrayList<SessionDTO>(sessions.size());
        for (Session session : sessions) {
            sessionDTOs.add(assemble(session));
        }
        return sessionDTOs;
    }

    public static SessionDTO assemble(final Session session) {
        final SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setUserName(session.getUser().getUserName());
        sessionDTO.setClientIpAddress(session.getClientIpAddress());
        sessionDTO.setCreationTime(session.getCreationTime());
        sessionDTO.setLastAccessTime(session.getLastAccessTime());
        sessionDTO.setSsoToken(session.getSSOToken().getToken());
        return sessionDTO;
    }
}
