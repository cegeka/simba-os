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
package org.simbasecurity.core.service.manager.assembler;

import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserEntity;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;
import org.simbasecurity.core.service.manager.dto.UserDTO;
import org.simbasecurity.test.LocatorTestCase;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserDTOAssemblerTest extends LocatorTestCase {

    @Before
    public void setup() {
        implantMock(UserValidator.class);
        implantMock(ConfigurationServiceImpl.class);
        implantMock(PasswordValidator.class);
    }

    @Test
    public void testAssembleSingleUser() {
        User user = createUser();

        UserDTO userData = UserDTOAssembler.assemble(user);

        assertNotNull(userData);
        assertEquals(0, userData.getId());
        assertEquals(user.getUserName(), userData.getUserName());
        assertEquals(user.getFirstName(), userData.getFirstName());
        assertEquals(user.getName(), userData.getName());
        assertEquals(user.getStatus(), userData.getStatus());
        assertEquals(user.getSuccessURL(), userData.getSuccessURL());
        assertEquals(user.getLanguage(), userData.getLanguage());
        assertEquals(user.isChangePasswordOnNextLogon(), userData.isChangePasswordOnNextLogon());

        assertEquals(0, user.getId());
        assertEquals(0, user.getVersion());
    }

    private User createUser() {
        User user = new UserEntity("username");
        user.setFirstName("first name");
        user.setName("name");
        user.setStatus(Status.ACTIVE);
        user.setSuccessURL("success url");
        user.setLanguage(Language.en_US);
        user.setChangePasswordOnNextLogon(true);
        return user;
    }

    @Test
    public void testAssembleMultipleUsers() {
        User user = createUser();

        Collection<UserDTO> userDataList = UserDTOAssembler.assemble(Arrays.asList(user));

        assertNotNull(userDataList);
        assertEquals(1, userDataList.size());

    }

}
