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
package org.simbasecurity.core.domain.repository;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.*;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.LocatorRule;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.domain.user.EmailAddress.email;

public class UserDatabaseRepositoryTest extends PersistenceTestCase {

    private static final String DUMMY_USER_NAME = "dummy";
    @Rule
    public LocatorRule locatorRule = LocatorRule.locator();
    protected CoreConfigurationService configurationServiceMock;
    private User user;

    @Autowired
    private UserDatabaseRepository userDatabaseRepository;

    @Before
    public void setUp() {
        user = UserTestBuilder.aUser().withUserName(DUMMY_USER_NAME).withFirstName("").withName("").withPassword("moo").withDateOfLastPasswordChange(new Date()).build();
        persistAndRefresh(user);
        setUpCommonLocatables();
    }

    public void setUpCommonLocatables() {
        locatorRule.implantMock(UserValidator.class);
        locatorRule.implantMock(PasswordValidator.class);

        configurationServiceMock = locatorRule.getCoreConfigurationService();
    }

    @Test
    public void test_findByName_success() {
        User result = userDatabaseRepository.findByName(DUMMY_USER_NAME);
        assertNotNull(result);
        assertEquals(DUMMY_USER_NAME, result.getUserName());
    }

    @Test
    public void findForRole() {
        Role role = new RoleEntity("aRole");
        User otherUser = UserTestBuilder.aDefaultUser().withUserName("otherUser").build();
        persistAndRefresh(role, otherUser);
        role.addUser(this.user);

        Collection<User> result = userDatabaseRepository.findForRole(role);
        assertThat(result).containsOnly(user);
    }

    @Test
    public void findAllOrderedByName_UsersAreOrderedByUserNameAscending() throws Exception {
        User userB = UserTestBuilder.aDefaultUser().withUserName("b").build();
        User userC = UserTestBuilder.aDefaultUser().withUserName("c").build();
        User userA = UserTestBuilder.aDefaultUser().withUserName("a").build();
        persistAndRefresh(userB, userC, userA);

        Collection<User> result = userDatabaseRepository.findAllOrderedByName();

        assertThat(result).containsExactly(userA, userB, userC, user);
    }

    @Test
    public void searchUsersOrderedByName_AllNameFieldsAreSearched() throws Exception {
        User userB = UserTestBuilder.aDefaultUser().withUserName("banaan").withFirstName("Jan").withName("Klaas").build();
        User userC = UserTestBuilder.aDefaultUser().withUserName("citroen").withFirstName("Piet").withName("Hein").build();
        User userA = UserTestBuilder.aDefaultUser().withUserName("appel").withFirstName("Kevin").withName("Nobin").build();
        persistAndRefresh(userB, userC, userA);

        assertThat(userDatabaseRepository.searchUsersOrderedByName("a")).containsExactly(userA, userB);
        assertThat(userDatabaseRepository.searchUsersOrderedByName("z")).isEmpty();
        assertThat(userDatabaseRepository.searchUsersOrderedByName("")).containsExactly(userA, userB, userC, user);
        assertThat(userDatabaseRepository.searchUsersOrderedByName("aa")).containsExactly(userB);
        assertThat(userDatabaseRepository.searchUsersOrderedByName("v")).containsExactly(userA);
        assertThat(userDatabaseRepository.searchUsersOrderedByName("s")).containsExactly(userB);
    }

    @Test
    public void searchUsersOrderedByName_SearchIsCaseInsensitiveOnAllNameFields() throws Exception {
        User user = UserTestBuilder.aDefaultUser().withUserName("banaan").withFirstName("Jan").withName("Klaas").build();
        persistAndRefresh(user);

        assertThat(userDatabaseRepository.searchUsersOrderedByName("j")).containsExactly(user);
        assertThat(userDatabaseRepository.searchUsersOrderedByName("S")).containsExactly(user);
        assertThat(userDatabaseRepository.searchUsersOrderedByName("B")).containsExactly(user);
    }

    @Test
    public void findUserByMail_WillReturnUser_IfPresentInDatabase() throws Exception {
        EmailAddress email = email("alfred@wayneindustries.com");

        User expectedUser = aDefaultUser().withEmail(email).build();
        persistAndRefresh(expectedUser);

        User user = userDatabaseRepository.findByEmail(email);

        Assertions.assertThat(user).isEqualTo(expectedUser);
    }

    @Test
    public void findUserByMailCaseInsensitive_WillReturnUser_IfPresentInDatabase() throws Exception {
        EmailAddress email = email("alfred@wayneindustries.com");
        EmailAddress otherEmail = email("Alfred@WayneIndustries.com");

        User expectedUser = aDefaultUser().withEmail(email).build();
        persistAndRefresh(expectedUser);

        User user = userDatabaseRepository.findByEmail(otherEmail);

        Assertions.assertThat(user).isEqualTo(expectedUser);
    }

    @Test
    public void findUserByMailCaseInsensitive2_WillReturnUser_IfPresentInDatabase() throws Exception {
        EmailAddress email = email("Alfred@WayneIndustries.com");
        EmailAddress otherEmail = email("alfred@wayneindustries.com");

        User expectedUser = aDefaultUser().withEmail(email).build();
        persistAndRefresh(expectedUser);

        User user = userDatabaseRepository.findByEmail(otherEmail);

        Assertions.assertThat(user).isEqualTo(expectedUser);
    }

    @Test
    public void findUserByMail_WillReturnUser_IfPresentInDatabase_AndActiveOrBlocked() throws Exception {
        EmailAddress email = email("alfred@wayneindustries.com");

        User expectedUser = aDefaultUser().withEmail(email).withStatus(Status.ACTIVE).build();
        persistAndRefresh(expectedUser);

        User user = userDatabaseRepository.findByEmail(email);

        Assertions.assertThat(user).isEqualTo(expectedUser);
    }

    @Test
    public void findUserByMail_WillNOTReturnUser_IfPresentInDatabase_AndInactive() throws Exception {
        EmailAddress email = email("morganfreeman@wayneindustries.com");


        User inactiveUser = aDefaultUser().withUserName("fmorgan").withEmail(email).withStatus(Status.INACTIVE).build();
        persistAndRefresh(inactiveUser);

        User user = userDatabaseRepository.findByEmail(email);

        Assertions.assertThat(user).isEqualTo(null);

    }

    @Test
    public void setEmail_StoredEmailIsNull_EmailIsNotNull() {
        Mockito.when(configurationServiceMock.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);
        User expectedUser = aDefaultUser().withoutEmail().build();
        persistAndRefresh(expectedUser);

        Optional<User> user = userDatabaseRepository.findById(expectedUser.getId());

        assertThat(user).isNotEmpty();
        assertThat(user.get().getEmail()).isNotNull();
    }

    @Test
    public void findById() {
        Optional<User> maybeUser = userDatabaseRepository.findById(user.getId());

        Assertions.assertThat(maybeUser).contains(user);
    }

}
