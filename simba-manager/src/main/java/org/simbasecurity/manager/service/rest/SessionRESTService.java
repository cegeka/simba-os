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
package org.simbasecurity.manager.service.rest;

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.api.service.thrift.SessionService;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.common.request.RequestUtil;
import org.simbasecurity.manager.service.rest.assembler.SessionDTOAssembler;
import org.simbasecurity.manager.service.rest.assembler.UserDTOAssembler;
import org.simbasecurity.manager.service.rest.dto.SessionDTO;
import org.simbasecurity.manager.service.rest.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Controller
@RequestMapping("session")
@Scope("request")
public class SessionRESTService extends BaseRESTService<SessionService.Client>{

	private final HttpServletRequest request;

	@Autowired
	public SessionRESTService(HttpServletRequest request) {
		super(new SessionService.Client.Factory(), SimbaConfiguration.getSessionServiceURL());
		this.request = request;
	}

	@ResponseBody
	@RequestMapping("findAllActive")
	public Collection<SessionDTO> findAllActive() {
        return SessionDTOAssembler.assemble($(() -> cl().findAllActive()));
    }

	@RequestMapping("remove")
	@ResponseBody
	public void remove(@RequestBody SessionDTO session) {
		if (getSSOToken().getToken().equals(session.getSsoToken())) {
            throw new IllegalArgumentException("You can't delete your own session!");
        }
        $(() -> cl().remove(session.getSsoToken()));
	}

	@RequestMapping("removeAllButMine")
	@ResponseBody
	public void removeAllButMine() {
        $(() -> cl().removeAllBut(getSSOToken().getToken()));
	}

	@ResponseBody
	@RequestMapping("getCurrentUser")
	public UserDTO getCurrentUser() {
        return UserDTOAssembler.assemble($(() -> cl().getUserFor(getSSOToken().getToken())));
	}

	private SSOToken getSSOToken() {
		return RequestUtil.getSsoTokenThatShouldBePresent(request);
	}
}
