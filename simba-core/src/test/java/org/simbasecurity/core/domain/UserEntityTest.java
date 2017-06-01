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
package org.simbasecurity.core.domain;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;
import org.simbasecurity.test.LocatorTestCase;

import java.util.Calendar;
import java.util.Date;

import static org.apache.commons.lang.time.DateUtils.truncate;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.exception.SimbaMessageKey.PASSWORD_INVALID_LENGTH;

public class UserEntityTest extends LocatorTestCase {
	private static final String INVALID_PASSWORD = "invalidpassword";
	private static final String DEFAULT_PASSWORD = "Simba3D";
	private static final String VALID_PASSWORD = "Simba2!";
	private static final String OLD_PASSWORD = "oldPassword";
	private static final String USERNAME = "Lenne";

	private UserEntity user;

	@Before
	public void setUp() {
		implantMock(UserValidator.class);

		PasswordValidator mockPasswordValidator = implantMock(PasswordValidator.class);

		doThrow(new SimbaException(PASSWORD_INVALID_LENGTH)).when(mockPasswordValidator).validatePassword(INVALID_PASSWORD);

		ConfigurationServiceImpl configurationServiceMock = implantMock(ConfigurationServiceImpl.class);
		when(configurationServiceMock.getValue(SimbaConfigurationParameter.DEFAULT_PASSWORD)).thenReturn(DEFAULT_PASSWORD);

		user = new UserEntity(USERNAME, null, null, null, Language.en_US, Status.ACTIVE, true, true);
	}

	@Test
	public void setStatus() {
		UserEntity entity = new UserEntity(USERNAME);
		assertEquals(Status.ACTIVE, entity.getStatus());
		assertNull(entity.getInactiveDate());

		entity.setStatus(Status.INACTIVE);
		assertEquals(Status.INACTIVE, entity.getStatus());
		Date inactiveDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		assertEquals(inactiveDate, entity.getInactiveDate());

		entity.setStatus(Status.INACTIVE);
		assertEquals(Status.INACTIVE, entity.getStatus());
		assertEquals(inactiveDate, entity.getInactiveDate());
	}

	@Test
	public void changePasswordAuthorized_oldPasswordIncorrect() {
		Assert.assertFalse(user.changePasswordAuthorized(OLD_PASSWORD, VALID_PASSWORD));
	}

	@Test(expected = SimbaException.class)
	public void changePasswordAuthorized_oldPasswordSameAsNewPassword() {
		user.changePasswordAuthorized(DEFAULT_PASSWORD, DEFAULT_PASSWORD);
	}

	@Test(expected = SimbaException.class)
	public void changePasswordAuthorized_newPasswordInvalid() {
		user.changePasswordAuthorized(DEFAULT_PASSWORD, INVALID_PASSWORD);
	}

	@Test
	public void changePasswordAuthorized_changeSuccess() {
		assertTrue(user.changePasswordAuthorized(DEFAULT_PASSWORD, VALID_PASSWORD));
		assertFalse(user.isChangePasswordOnNextLogon());
		assertEquals(truncate(new Date(), Calendar.DAY_OF_MONTH), user.getDateOfLastPasswordChange());
	}

	@Test(expected = SimbaException.class)
	public void changePassword_passwordsDontMatch() {
		user.changePassword(OLD_PASSWORD, VALID_PASSWORD);
	}

	@Test(expected = IllegalArgumentException.class)
	public void changePassword_newPasswordNull() {
		user.changePassword(null, null);
	}

	@Test(expected = SimbaException.class)
	public void changePassword_passwordConfirmationInvalid() {
		user.changePassword(DEFAULT_PASSWORD, INVALID_PASSWORD);
	}

	@Test
	public void changePassword_changeSuccess() {
		UserEntity entity = new UserEntity(USERNAME, null, null, null, Language.nl_NL, Status.ACTIVE, true, true);
		entity.changePassword(VALID_PASSWORD, VALID_PASSWORD);
		assertFalse(entity.isChangePasswordOnNextLogon());
		assertEquals(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), entity.getDateOfLastPasswordChange());
	}

	@Test
	public void resetPassword() {
		user.resetPassword();
		assertTrue(user.isChangePasswordOnNextLogon());
		assertTrue(user.checkPassword(DEFAULT_PASSWORD));
	}
}
