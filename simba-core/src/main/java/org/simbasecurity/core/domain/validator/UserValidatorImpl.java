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
package org.simbasecurity.core.domain.validator;

import static org.apache.commons.lang.StringUtils.*;
import static org.simbasecurity.core.config.ConfigurationParameter.*;
import static org.simbasecurity.core.exception.SimbaMessageKey.*;

import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.locator.GlobalContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserValidatorImpl implements UserValidator {

	@Autowired private ConfigurationService configurationService;

	@Override
	public void validateUserName(String userName) {
		UserNameValidator userNameValidator = GlobalContext.locate(UserNameValidator.class);
		userNameValidator.validateUserName(userName);
	}

	@Override
	public void validateFirstName(String firstName) {

		if (isEmpty(firstName)) {
			return;
		}

		Integer minLength = configurationService.getValue(FIRSTNAME_MIN_LENGTH);

		if (firstName.length() < minLength) {
			throw new SimbaException(FIRSTNAME_TOO_SHORT, minLength.toString());
		}

		Integer maxLength = configurationService.getValue(FIRSTNAME_MAX_LENGTH);

		if (firstName.length() > maxLength) {
			throw new SimbaException(FIRSTNAME_TOO_LONG, maxLength.toString());
		}
	}

	@Override
	public void validateName(String name) {

		if (isEmpty(name)) {
			return;
		}

		Integer minLength = configurationService.getValue(LASTNAME_MIN_LENGTH);

		if (name.length() < minLength) {
			throw new SimbaException(NAME_TOO_SHORT, minLength.toString());
		}

		Integer maxLength = configurationService.getValue(LASTNAME_MAX_LENGTH);

		if (name.length() > maxLength) {
			throw new SimbaException(NAME_TOO_LONG, maxLength.toString());
		}
	}

	@Override
	public void validateSuccessURL(String successURL) {
		if (isEmpty(successURL)) {
			return;
		}

		Integer maxLength = configurationService.getValue(SUCCESSURL_MAX_LENGTH);

		if (successURL.length() > maxLength) {
			throw new SimbaException(SUCCESSURL_TOO_LONG, maxLength.toString());
		}
	}

	@Override
	public void validateLanguage(Language language) {
		if (language == null) {
			throw new SimbaException(LANGUAGE_EMPTY);
		}
	}

	@Override
	public void validateStatus(Status status) {
		if (status == null) {
			throw new SimbaException(STATUS_EMPTY);
		}
	}

}
