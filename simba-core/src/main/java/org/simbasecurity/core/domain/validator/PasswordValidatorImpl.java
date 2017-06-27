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
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.*;
import static org.simbasecurity.core.exception.SimbaMessageKey.PASSWORD_INVALID_COMPLEXITY;
import static org.simbasecurity.core.exception.SimbaMessageKey.PASSWORD_INVALID_LENGTH;

@Component
public class PasswordValidatorImpl implements PasswordValidator {

    private CoreConfigurationService configurationService;

    public void validatePassword(String newPassword) throws SimbaException {
        if (!checkPasswordLength(newPassword)) {
            throw new SimbaException(PASSWORD_INVALID_LENGTH);
        }
        if (!hasValidCharacters(newPassword) || !isComplex(newPassword)) {
            throw new SimbaException(PASSWORD_INVALID_COMPLEXITY);
        }
    }

    private boolean hasValidCharacters(String newPassword) {
        String validCharacters = configurationService.getValue(PASSWORD_VALID_CHARACTERS);
        return newPassword.matches(validCharacters);
    }

    private boolean checkPasswordLength(String newPassword) {
        Integer minimumLength = configurationService.getValue(PASSWORD_MIN_LENGTH);
        Integer maximumLength = configurationService.getValue(PASSWORD_MAX_LENGTH);
        return newPassword != null && newPassword.length() >= minimumLength && newPassword.length() <= maximumLength;
    }

    private boolean isComplex(String password) {
        Integer minimumComplexity = configurationService.getValue(PASSWORD_MINIMUM_COMPLEXITY);
        return checkPasswordFollowsComplexityRules(password) >= minimumComplexity;
    }

    private int checkPasswordFollowsComplexityRules(String password) {
        List<String> complexityRules = configurationService.getValue(PASSWORD_COMPLEXITY_RULE);

        int complexity = 0;

        for (String rule : complexityRules) {
            if (password.matches(rule)) {
                complexity++;
            }
        }
        return complexity;
    }

    @Autowired
    public void setConfigurationService(CoreConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
