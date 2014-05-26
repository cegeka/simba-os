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
package org.simbasecurity.core.service.manager;

import static org.simbasecurity.core.service.manager.assembler.SessionDTOAssembler.assemble;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.common.request.RequestUtil;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.repository.SessionRepository;
import org.simbasecurity.core.service.manager.assembler.UserDTOAssembler;
import org.simbasecurity.core.service.manager.dto.SessionDTO;
import org.simbasecurity.core.service.manager.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Transactional
@Controller
@RequestMapping("session")
@Scope("request")
public class SessionManagerService {

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private HttpServletRequest request;

	@ResponseBody
	@RequestMapping("findAllActive")
	public Collection<SessionDTO> findAllActive() {
		return assemble(sessionRepository.findAllActive());
	}

	@RequestMapping("remove")
	@ResponseBody
	public void remove(@RequestBody SessionDTO session) {
		Session selectedSession = sessionRepository.lookUp(session);
		if (selectedSession.getUser().getUserName().equalsIgnoreCase(getCurrentUser().getUserName())) {
			throw new IllegalArgumentException("You can't delete your own session!");
		}
		sessionRepository.remove(selectedSession);
	}

	@RequestMapping("removeAllButMine")
	@ResponseBody
	public void removeAllButMine() {
		sessionRepository.removeAllBut(getSSOToken());
	}

	@ResponseBody
	@RequestMapping("getCurrentUser")
	public UserDTO getCurrentUser() {
		Session session = sessionRepository.findBySSOToken(getSSOToken());
		return UserDTOAssembler.assemble(session.getUser());
	}

	private SSOToken getSSOToken() {
		return RequestUtil.getSsoTokenThatShouldBePresent(request);
	}
}
