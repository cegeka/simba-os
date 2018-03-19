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

import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.*;
import static org.simbasecurity.core.exception.SimbaMessageKey.*;

@Component
public class UserValidatorImpl implements UserValidator {

    private final CoreConfigurationService configurationService;
    private final UserNameValidator userNameValidator;

    @Autowired
    public UserValidatorImpl(CoreConfigurationService configurationService, UserNameValidator userNameValidator) {
        this.configurationService = configurationService;
        this.userNameValidator = userNameValidator;
    }

    @Override
    public void validateUserName(String userName) {
        userNameValidator.validateUserName(userName);
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private void validate(String name, int minLength, int maxLength, SimbaMessageKey toShortMessageKey,
                          SimbaMessageKey toLongMessageKey) {
        if (isEmpty(name)) return;
        if (name.length() < minLength) throw new SimbaException(toShortMessageKey, String.valueOf(minLength));
        if (name.length() > maxLength) throw new SimbaException(toLongMessageKey, String.valueOf(maxLength));
    }

    @Override
    public void validateFirstName(String firstName) {
        validate(firstName, configurationService.getValue(FIRSTNAME_MIN_LENGTH),
                configurationService.getValue(FIRSTNAME_MAX_LENGTH), FIRSTNAME_TOO_SHORT, FIRSTNAME_TOO_LONG);
    }

    @Override
    public void validateName(String name) {
        validate(name, configurationService.getValue(LASTNAME_MIN_LENGTH),
                configurationService.getValue(LASTNAME_MAX_LENGTH), NAME_TOO_SHORT, NAME_TOO_LONG);
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
