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
package org.simbasecurity.core.service.user;

import static org.simbasecurity.core.service.ErrorSender.sendError;
import static org.simbasecurity.core.service.ErrorSender.sendUnauthorizedError;

import javax.servlet.http.HttpServletResponse;

import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.SessionRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.ErrorSender;
import org.simbasecurity.core.service.manager.dto.ChangePasswordDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Can be called via rest by the user itself, not the admin or manager.
 */
@Transactional
@Controller
@RequestMapping("myDetails")
public class UserService {

	@Autowired
	private AuthorizationService.Iface authorizationService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private ConfigurationService configurationService;

	@RequestMapping("changePassword")
	@ResponseBody
	public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, HttpServletResponse response) {
		if (changePasswordDTO.getSsoToken() == null || changePasswordDTO.getUserName() == null) {
			sendUnauthorizedError(response);
			return;
		}

		Session activeSession = sessionRepository.findBySSOToken(new SSOToken(changePasswordDTO.getSsoToken()));
		if (activeSession == null) {
			sendUnauthorizedError(response);
			return;

		} else {

			User sessionUser = activeSession.getUser();
			User userThatNeedsPasswordChange = userRepository.findByName(changePasswordDTO.getUserName());
			if (!sessionUser.getUserName().equals(userThatNeedsPasswordChange.getUserName())) {
				sendUnauthorizedError(response);
				return;

			} else {
				try {
					userThatNeedsPasswordChange.changePassword(changePasswordDTO.getNewPassword(), changePasswordDTO.getNewPasswordConfirmation());
				} catch (SimbaException ex) {
					sendError(ErrorSender.UNABLE_TO_CHANGE_PASSWORD_ERROR_CODE, response, ex.getMessage());
					return;
				}

				userRepository.flush();
			}
		}
	}
}
