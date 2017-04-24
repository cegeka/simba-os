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

import static org.mockito.Mockito.*;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.exception.SimbaException;

@RunWith(MockitoJUnitRunner.class)
public class UserValidatorImplTest {

	@Mock private ConfigurationService mockConfigurationService;

    @InjectMocks
	private UserValidatorImpl validator;

	@Test
	public void validateFirstName_firstNameNull() {
		validator.validateFirstName(null);
	}

	@Test
	public void validateFirstName_firstNameEmptyString() {
		validator.validateFirstName("");
	}

	@Test(expected = SimbaException.class)
	public void validateFirstName_firstNameTooShort() {
		Mockito.when(mockConfigurationService.getValue(FIRSTNAME_MIN_LENGTH)).thenReturn(3);

		validator.validateFirstName("Fi");
	}

	@Test(expected = SimbaException.class)
	public void validateFirstName_firstNameTooLong() {
		when(mockConfigurationService.getValue(FIRSTNAME_MIN_LENGTH)).thenReturn(3);
		when(mockConfigurationService.getValue(FIRSTNAME_MAX_LENGTH)).thenReturn(5);

		validator.validateFirstName("Firstn");
	}

	@Test
	public void validateLastName_lastNameNull() {
		validator.validateName(null);
	}

	@Test
	public void validateLastName_lastNameEmptyString() {
		validator.validateName("");
	}

	@Test(expected = SimbaException.class)
	public void validateLastName_lastNameTooShort() {
		Mockito.when(mockConfigurationService.getValue(LASTNAME_MIN_LENGTH)).thenReturn(3);

		validator.validateName("La");
	}

	@Test(expected = SimbaException.class)
	public void validateLastName_lastnameTooLong() {
		when(mockConfigurationService.getValue(LASTNAME_MIN_LENGTH)).thenReturn(3);
		when(mockConfigurationService.getValue(LASTNAME_MAX_LENGTH)).thenReturn(5);

		validator.validateName("Lastna");
	}

	@Test
	public void validateSuccessURL_successURLNull() {
		validator.validateSuccessURL(null);
	}

	@Test
	public void validateSuccessURL_successURLEmptyString() {
		validator.validateSuccessURL("");
	}

	@Test(expected = SimbaException.class)
	public void validateSuccessURL_successURLTooLong() {
		when(mockConfigurationService.getValue(SUCCESSURL_MAX_LENGTH)).thenReturn(5);

		validator.validateSuccessURL("SuccessURL");
	}

	@Test(expected = SimbaException.class)
	public void validateStatus_statusNull() {
		validator.validateStatus(null);
	}

	@Test(expected = SimbaException.class)
	public void validateLanguage_languageNull() {
		validator.validateLanguage(null);
	}

}
