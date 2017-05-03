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

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.exception.SimbaException;

/**
 * Complexity Settings: ('PASSWORD_MAX_LENGTH','15');
 * ('PASSWORD_MIN_LENGTH','6'); ('PASSWORD_VALID_CHARACTERS','[\x21-\x7E]*');
 * ('PASSWORD_COMPLEXITY_RULE','.*[A-Z].*');
 * ('PASSWORD_COMPLEXITY_RULE','.*[a-z].*');
 * ('PASSWORD_COMPLEXITY_RULE','.*[0-9].*');
 * ('PASSWORD_COMPLEXITY_RULE','.*[\W_].*');
 * ('PASSWORD_MINIMUM_COMPLEXITY','3');
 * 
 * Value: Bla0rr3
 */
@RunWith(MockitoJUnitRunner.class)
public class PasswordValidatorImplTest {

	@Mock
	private ConfigurationService configurationService; // it's a mock but it
														// mimics the real
														// values

	@InjectMocks
	private PasswordValidatorImpl validator;

	@Before
	public void setupTheComplexityRules() {
		when(configurationService.getValue(SimbaConfigurationParameter.PASSWORD_MIN_LENGTH)).thenReturn(6);
		when(configurationService.getValue(SimbaConfigurationParameter.PASSWORD_MAX_LENGTH)).thenReturn(15);
		when(configurationService.getValue(SimbaConfigurationParameter.PASSWORD_VALID_CHARACTERS)).thenReturn("[\\x21-\\x7E]*");

		List<String> complexityRules = new ArrayList<String>();
		complexityRules.add(".*[A-Z].*");
		complexityRules.add(".*[a-z].*");
		complexityRules.add(".*[\\W_].*");
		complexityRules.add(".*[0-9].*");

		when(configurationService.getValue(SimbaConfigurationParameter.PASSWORD_COMPLEXITY_RULE)).thenReturn(complexityRules);
		when(configurationService.getValue(SimbaConfigurationParameter.PASSWORD_MINIMUM_COMPLEXITY)).thenReturn(3);
	}

	@Test
	public void testPasswordComplexity_ok() {
		validator.validatePassword("Blarr3");
		validator.validatePassword("Blarr_3");
		validator.validatePassword("Bla0rr3");
		validator.validatePassword("_Blarr_3");
		validator.validatePassword("Bla_rr_3");
		validator.validatePassword("AaBb1Cc2Dd_3");
	}

	@Test(expected = SimbaException.class)
	public void testPasswordComplexity_onlySmallAndDigit_notOk() {
		validator.validatePassword("ssssssssss1");
	}

	@Test(expected = SimbaException.class)
	public void testPasswordComplexity_onlyCapsAndDigit_notOk() {
		validator.validatePassword("SSSSSSSSSS1");
	}

	@Test(expected = SimbaException.class)
	public void testPasswordComplexity_onlyChars_notOk() {
		validator.validatePassword("OnlyCharsss");
	}

	@Test(expected = SimbaException.class)
	public void testPasswordComplexity_tooShort_notOk() {
		validator.validatePassword("Short");
	}

	@Test(expected = SimbaException.class)
	public void testPasswordComplexity_tooLong_notOk() {
		validator.validatePassword("Loooooooooooooooooooooooooooooooooooooong");
	}

}
