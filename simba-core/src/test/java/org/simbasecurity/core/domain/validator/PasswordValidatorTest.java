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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasswordValidatorTest {

    private static final List<String> PASSWORD_RULES = Arrays.asList(".*[A-Z].*", ".*[a-z].*", ".*[0-9].*", ".*[\\W_].*");
    private static final String PASSWORD_VALID_CHARS = "[\\x21-\\x7E]*";
    private static final Integer PASSWORD_MIN_COMPLEXITY = 3;
    private static final Integer PASSWORD_MAX_LENGTH = 15;
    private static final Integer PASSWORD_MIN_LENGTH = 6;

    @Mock private ConfigurationServiceImpl configurationService;

    @InjectMocks
    private PasswordValidatorImpl validator;

    @Before
    public void setup() throws Exception {
        when(configurationService.getValue(SimbaConfigurationParameter.PASSWORD_MIN_LENGTH)).thenReturn(PASSWORD_MIN_LENGTH);
        when(configurationService.getValue(SimbaConfigurationParameter.PASSWORD_MAX_LENGTH)).thenReturn(PASSWORD_MAX_LENGTH);
        when(configurationService.getValue(SimbaConfigurationParameter.PASSWORD_MINIMUM_COMPLEXITY)).thenReturn(PASSWORD_MIN_COMPLEXITY);
        when(configurationService.getValue(SimbaConfigurationParameter.PASSWORD_VALID_CHARACTERS)).thenReturn(PASSWORD_VALID_CHARS);
        when(configurationService.getValue(SimbaConfigurationParameter.PASSWORD_COMPLEXITY_RULE)).thenReturn(PASSWORD_RULES);
    }

    @Test(expected = SimbaException.class)
    public void passwordTooShort() throws Exception {
        validator.validatePassword("aB7aB");
    }

    @Test(expected = SimbaException.class)
    public void passwordTooLong() throws Exception {
        validator.validatePassword("aB7aB7aB7aB7aB7a");
    }

    @Test(expected = SimbaException.class)
    public void passwordContainsInvalidCharacters() throws Exception {
        validator.validatePassword("aB7 aB7");
    }

    @Test(expected = SimbaException.class)
    public void passwordNotComplexEnough() throws Exception {
        validator.validatePassword("aBaBaB");
    }

    @Test
    public void testPasswordLengthBoundaries() throws Exception {
        validator.validatePassword("aB7aB7");
        validator.validatePassword("aB7aB7aB7aB7aB7");
    }

    @Test
    public void testPasswordComplexityBoundaries() throws Exception {
        validator.validatePassword("aB+aB-");
    }

    @Test
    public void testPasswordValidCharactersBoundaries() throws Exception {
        validator.validatePassword("!aBcD~");
    }

    @Test
    public void testPasswordComplexityWithUnderscoreAsNonWordCharacterSucceeds() throws Exception {
        validator.validatePassword("Ab__bA");
    }

    @Test(expected = SimbaException.class)
    public void testPasswordComplexityWithUnderscoreAsNonWordCharacterFails() throws Exception {
        validator.validatePassword("A$__!A");
    }
}
