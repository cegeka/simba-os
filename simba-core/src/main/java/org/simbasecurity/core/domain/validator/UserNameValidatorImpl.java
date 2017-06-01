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
package org.simbasecurity.core.domain.validator;

import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.*;
import static org.simbasecurity.core.exception.SimbaMessageKey.*;

@Component
public class UserNameValidatorImpl implements UserNameValidator {

	@Autowired private ConfigurationServiceImpl configurationService;

	@Override
	public void validateUserName(String userName) {

		if (isEmpty(userName)) {
			throw new SimbaException(USERNAME_EMPTY);
		}

		Integer minLength = configurationService.getValue(USERNAME_MIN_LENGTH);

		if (userName.length() < minLength) {
			throw new SimbaException(USERNAME_TOO_SHORT, minLength.toString());
		}

		Integer maxLength = configurationService.getValue(USERNAME_MAX_LENGTH);

		if (userName.length() > maxLength) {
			throw new SimbaException(USERNAME_TOO_LONG, maxLength.toString());
		}

		String userRegex = configurationService.getValue(USERNAME_REGEX);

		if (!userName.matches(userRegex)) {
			throw new SimbaException(USERNAME_INVALID);
		}
	}
}
