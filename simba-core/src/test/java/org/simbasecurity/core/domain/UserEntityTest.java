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
import org.junit.Rule;
import org.junit.Test;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.AutowirerRule;
import org.simbasecurity.test.LocatorRule;

import java.util.Calendar;
import java.util.Date;

import static org.apache.commons.lang.time.DateUtils.truncate;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.domain.UserTestBuilder.aUser;
import static org.simbasecurity.core.exception.SimbaMessageKey.PASSWORD_INVALID_LENGTH;

public class UserEntityTest {

    private static final String INVALID_PASSWORD = "invalidpassword";
    private static final String DEFAULT_PASSWORD = "Simba3D";
    private static final String VALID_PASSWORD = "Simba2!";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String USERNAME = "Lenne";

    @Rule public LocatorRule locatorRule = LocatorRule.locator();
    @Rule public AutowirerRule autowirerRule = AutowirerRule.autowirer();

    private User user;

    @Before
    public void setUp() {
        autowirerRule.mockBean(UserValidator.class);
        PasswordValidator mockPasswordValidator = autowirerRule.mockBean(PasswordValidator.class);

        CoreConfigurationService coreConfigurationService = locatorRule.getCoreConfigurationService();
        when(coreConfigurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(true);

        doThrow(new SimbaException(PASSWORD_INVALID_LENGTH)).when(mockPasswordValidator).validatePassword(INVALID_PASSWORD);

        user = aUser().withUserName(USERNAME).withStatus(Status.ACTIVE).withChangePasswordOnNextLogon(true).withPasswordChangeRequired(true).buildWithValidation();
    }

    @Test
    public void setStatus() {
        User user = aDefaultUser().withUserName(USERNAME).buildWithValidation();
        assertEquals(Status.ACTIVE, user.getStatus());
        assertNull(user.getInactiveDate());

        user.setStatus(Status.INACTIVE);
        assertEquals(Status.INACTIVE, user.getStatus());
        Date inactiveDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        assertEquals(inactiveDate, user.getInactiveDate());

        user.setStatus(Status.INACTIVE);
        assertEquals(Status.INACTIVE, user.getStatus());
        assertEquals(inactiveDate, user.getInactiveDate());
    }

    @Test
    public void changePasswordAuthorized_oldPasswordIncorrect() {
        Assert.assertFalse(user.changePasswordAuthorized(OLD_PASSWORD, VALID_PASSWORD));
    }

    @Test
    public void changePasswordAuthorized_oldPasswordSameAsNewPassword() {
        user.changePasswordAuthorized(null, VALID_PASSWORD);
        assertThatThrownBy(() -> user.changePasswordAuthorized(VALID_PASSWORD, VALID_PASSWORD))
                .isInstanceOf(SimbaException.class)
                .hasMessage("PASSWORD_SAME_AS_OLD");
    }

    @Test
    public void changePasswordAuthorized_newPasswordInvalid() {
        assertThatThrownBy(() -> user.changePasswordAuthorized(null, INVALID_PASSWORD))
                .isInstanceOf(SimbaException.class)
                .hasMessage("PASSWORD_INVALID_LENGTH");
    }

    @Test
    public void changePasswordAuthorized_changeSuccess() {
        assertTrue(user.changePasswordAuthorized(null, VALID_PASSWORD));
        assertFalse(user.isChangePasswordOnNextLogon());
        assertEquals(truncate(new Date(), Calendar.DAY_OF_MONTH), user.getDateOfLastPasswordChange());
    }

    @Test
    public void changePassword_passwordsDontMatch() {
        assertThatThrownBy(() -> user.changePassword(OLD_PASSWORD, VALID_PASSWORD))
                .isInstanceOf(SimbaException.class)
                .hasMessage("PASSWORDS_DONT_MATCH");
    }

    @Test
    public void changePassword_newPasswordNull() {
        assertThatThrownBy(() -> user.changePassword(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("New password should not be null");
    }

    @Test
    public void changePassword_passwordConfirmationInvalid() {
        assertThatThrownBy(() -> user.changePassword(VALID_PASSWORD, INVALID_PASSWORD))
                .isInstanceOf(SimbaException.class)
                .hasMessage("PASSWORDS_DONT_MATCH");
    }

    @Test
    public void changePassword_changeSuccess() {
        User entity = aUser().withUserName(USERNAME).withLanguage(Language.nl_NL).withStatus(Status.ACTIVE).withChangePasswordOnNextLogon(true).withPasswordChangeRequired(true).build();
        entity.changePassword(VALID_PASSWORD, VALID_PASSWORD);
        assertFalse(entity.isChangePasswordOnNextLogon());
        assertEquals(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), entity.getDateOfLastPasswordChange());
    }



}
