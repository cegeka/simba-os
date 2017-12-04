package org.simbasecurity.core.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UserForTest extends UserEntity {

    private long id;
    private String userName;
    private String name;
    private String firstName;
    private Status status;
    private Date inactiveDate;
    private String successURL;
    private String password;
    private boolean changePasswordOnNextLogon;
    private boolean passwordChangeRequired;
    private boolean databaseLoginBlocked;
    private Date dateOfLastPasswordChange;
    private int invalidLoginCount;
    private Language language;
    private Set<Role> roles = new HashSet<>();
    private Set<Session> sessions = new HashSet<>();
    private Set<Group> groups = new HashSet<>();

    private UserForTest(){}

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public void setSuccessURL(String successURL) {
        this.successURL = successURL;
    }

    public static class UserTestBuilder {
        private UserForTest userForTest;
        private UserTestBuilder() {
            userForTest = new UserForTest();
        }
        public static UserTestBuilder aUser() {
            return new UserTestBuilder();
        }

        public User build() {
            return userForTest;
        }

        public UserTestBuilder withId(long id) {
            userForTest.id = id;
            return this;
        }

        public UserTestBuilder withUserName(String userName) {
            userForTest.userName = userName;
            return this;
        }

        public UserTestBuilder withName(String name) {
            userForTest.name = name;
            return this;
        }

        public UserTestBuilder withFirstName(String firstName) {
            userForTest.firstName = firstName;
            return this;
        }

        public UserTestBuilder withStatus(Status status) {
            userForTest.status = status;
            return this;
        }

        public UserTestBuilder withInactiveDate(Date inactiveDate) {
            userForTest.inactiveDate = inactiveDate;
            return this;
        }

        public UserTestBuilder withSuccessURL(String successURL) {
            userForTest.successURL = successURL;
            return this;
        }

        public UserTestBuilder withPassword(String password) {
            userForTest.password = password;
            return this;
        }

        public UserTestBuilder withChangePasswordOnNextLogon(boolean changePasswordOnNextLogon) {
            userForTest.changePasswordOnNextLogon = changePasswordOnNextLogon;
            return this;
        }

        public UserTestBuilder withPasswordChangeRequired(boolean passwordChangeRequired) {
            userForTest.passwordChangeRequired = passwordChangeRequired;
            return this;
        }

        public UserTestBuilder withDatabaseLoginBlocked(boolean databaseLoginBlocked) {
            userForTest.databaseLoginBlocked = databaseLoginBlocked;
            return this;
        }

        public UserTestBuilder withDateOfLastPasswordChange(Date dateOfLastPasswordChange) {
            userForTest.dateOfLastPasswordChange = dateOfLastPasswordChange;
            return this;
        }

        public UserTestBuilder withInvalidLoginCount(int invalidLoginCount) {
            userForTest.invalidLoginCount = invalidLoginCount;
            return this;
        }

        public UserTestBuilder withLanguage(Language language) {
            userForTest.language = language;
            return this;
        }

        public UserTestBuilder withRoles(Set<Role> roles) {
            userForTest.roles = roles;
            return this;
        }

        public UserTestBuilder withSessions(Set<Session> sessions) {
            userForTest.sessions = sessions;
            return this;
        }

        public UserTestBuilder withGroups(Set<Group> groups) {
            userForTest.groups = groups;
            return this;
        }
    }
}
