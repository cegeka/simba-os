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

import org.simbasecurity.api.service.thrift.SessionService;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.manager.service.rest.assembler.DTOAssembler;
import org.simbasecurity.manager.service.rest.dto.SessionDTO;
import org.simbasecurity.manager.service.rest.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;

@Controller
@RequestMapping("session")
@Scope("request")
public class SessionRESTService extends BaseRESTService<SessionService.Client>{


	public SessionRESTService() {
		super(new SessionService.Client.Factory(), SimbaConfiguration.getSessionServiceURL());
	}

	@ResponseBody
	@RequestMapping("findAllActive")
	public Collection<SessionDTO> findAllActive(@CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.list($(() -> cl(ssoToken).findAllActive()));
    }

	@RequestMapping("remove")
	@ResponseBody
	public void remove(@RequestBody SessionDTO session,
					   @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
		if (ssoToken.equals(session.getSsoToken())) {
            throw new IllegalArgumentException("You can't delete your own session!");
        }
        $(() -> cl(ssoToken).remove(session.getSsoToken()));
	}

	@RequestMapping("removeAllButMine")
	@ResponseBody
	public void removeAllButMine(@CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).removeAllBut(ssoToken));
	}

	@ResponseBody
	@RequestMapping("getCurrentUser")
	public UserDTO getCurrentUser(@CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return DTOAssembler.assemble($(() -> cl(ssoToken).getUserFor(ssoToken)));
	}
}
