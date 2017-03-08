/*
 * Copyright 2013 Simba Open Source
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

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.exception.SimbaException;

import static org.mockito.Mockito.when;
import static org.simbasecurity.core.config.ConfigurationParameter.*;

public class UserNameValidatorImplTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private ConfigurationService mockConfigurationService;

    @InjectMocks
    private UserNameValidatorImpl validator;

    @Test(expected = SimbaException.class)
    public void validateUserName_usernameNull() {
        validator.validateUserName(null);
    }

    @Test(expected = SimbaException.class)
    public void validateUserName_usernameEmptyString() {
        validator.validateUserName("");
    }

    @Test(expected = SimbaException.class)
    public void validateUserName_usernameTooShort() {
        Mockito.when(mockConfigurationService.getValue(USERNAME_MIN_LENGTH)).thenReturn(3);

        validator.validateUserName("Us");
    }

    @Test(expected = SimbaException.class)
    public void validateUserName_usernameTooLong() {
        when(mockConfigurationService.getValue(USERNAME_MIN_LENGTH)).thenReturn(3);
        when(mockConfigurationService.getValue(USERNAME_MAX_LENGTH)).thenReturn(5);

        validator.validateUserName("Userna");
    }

    @Test(expected = SimbaException.class)
    public void validateUserName_usernameInvalidCharacters() {
        when(mockConfigurationService.getValue(USERNAME_MIN_LENGTH)).thenReturn(3);
        when(mockConfigurationService.getValue(USERNAME_MAX_LENGTH)).thenReturn(5);

        validator.validateUserName("User{}");
    }

    @Test(expected = SimbaException.class)
    public void validateUserName_usernameValid() {
        when(mockConfigurationService.getValue(USERNAME_MIN_LENGTH)).thenReturn(3);
        when(mockConfigurationService.getValue(USERNAME_MAX_LENGTH)).thenReturn(10);

        validator.validateUserName("User_name09");
    }
}
