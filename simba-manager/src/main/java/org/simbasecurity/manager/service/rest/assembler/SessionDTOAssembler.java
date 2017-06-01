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
package org.simbasecurity.manager.service.rest.assembler;

import org.simbasecurity.api.service.thrift.SessionR;
import org.simbasecurity.manager.service.rest.dto.SessionDTO;

import java.util.Collection;
import java.util.stream.Collectors;

public final class SessionDTOAssembler {
    private SessionDTOAssembler() {
    }

    public static Collection<SessionDTO> assemble(Collection<SessionR> sessions) {
        return sessions.stream()
                       .map(SessionDTOAssembler::assemble)
                       .collect(Collectors.toList());
    }

    public static SessionDTO assemble(SessionR session) {
        final SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setUserName(session.getUserName());
        sessionDTO.setClientIpAddress(session.getClientIpAddress());
        sessionDTO.setCreationTime(session.getCreationTime());
        sessionDTO.setLastAccessTime(session.getLastAccessTime());
        sessionDTO.setSsoToken(session.getSsoToken());
        return sessionDTO;
    }
}
