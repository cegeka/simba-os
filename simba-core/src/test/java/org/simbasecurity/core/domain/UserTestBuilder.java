package org.simbasecurity.core.domain;

import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.domain.user.EmailFactory;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.util.ReflectionUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class UserTestBuilder {
    public static final String NAME = "Wayne";
    public static final String FIRST_NAME = "bruce";
    public static final String EMAIL = "bruce@wayneindustries.com";
    public static final String PASSWORD = "iamthebatman";
    public static final Language LANGUAGE = Language.en_US;

    private String name;
    private String firstName;
    private Status status;
    private String successURL;
    private boolean changePasswordOnNextLogon;
    private boolean passwordChangeRequired;
    private Language language;
    private long id;
    private String userName;
    private Date inactiveDate;
    private String password;
    private boolean databaseLoginBlocked;
    private Date dateOfLastPasswordChange;
    private int invalidLoginCount;
    private Set<Role> roles = new HashSet<>();
    private Set<Session> sessions = new HashSet<>();
    private Set<Group> groups = new HashSet<>();
    private EmailAddress email;

    private EmailFactory emailFactory;


    private UserTestBuilder() {
        CoreConfigurationService coreConfigurationService = new ConfigurationServiceImpl() {
            @Override
            public <T> T getValue(ConfigurationParameter parameter) {
                if (parameter == SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED) return (T) Boolean.FALSE;
                return null;
            }
        };

        emailFactory = new EmailFactory(coreConfigurationService);
    }

    private UserTestBuilder(EmailFactory emailFactory) {
        this.emailFactory = emailFactory;
    }

    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }

    public static UserTestBuilder aUser(EmailFactory emailFactory) {
        return new UserTestBuilder(emailFactory);
    }

    public static UserTestBuilder aDefaultUser() {
        return aUser()
                .withName(NAME)
                .withFirstName(FIRST_NAME)
                .withEmail(EMAIL)
                .withPassword(PASSWORD)
                .withDateOfLastPasswordChange(new Date())
                .withStatus(Status.ACTIVE)
                .withPasswordChangeRequired(true)
                .withChangePasswordOnNextLogon(true)
                .withLanguage(LANGUAGE);
    }

    public static UserTestBuilder aDefaultUser(EmailFactory emailFactory) {
        return aUser(emailFactory)
                .withName(NAME)
                .withFirstName(FIRST_NAME)
                .withEmail(EMAIL)
                .withPassword(PASSWORD)
                .withDateOfLastPasswordChange(new Date())
                .withStatus(Status.ACTIVE)
                .withPasswordChangeRequired(true)
                .withChangePasswordOnNextLogon(true)
                .withLanguage(LANGUAGE);
    }

    /**
     * Use this build method if you NEED a UserEntity that does all of the validations.
     * Note that you'll need to set up your test with e.g. <code>implantMock(UserValidator.class)</code>
     */
    public User buildWithValidation() {
        return UserEntity.user(userName,firstName,name,successURL,language,status,changePasswordOnNextLogon,passwordChangeRequired,email);
    }

    public User build() {
        UserEntity user = new UserEntity();
        user.setChangePasswordOnNextLogon(changePasswordOnNextLogon);
        user.setPasswordChangeRequired(passwordChangeRequired);
        user.setEmail(email);
        ReflectionUtil.setField(user, "name", name);
        ReflectionUtil.setField(user, "firstName", firstName);
        ReflectionUtil.setField(user, "language", language);
        ReflectionUtil.setField(user, "status", status);
        ReflectionUtil.setField(user, "successURL", successURL);
        ReflectionUtil.setField(user, "id", id);
        ReflectionUtil.setField(user, "userName", userName);
        ReflectionUtil.setField(user, "inactiveDate", inactiveDate);
        ReflectionUtil.setField(user, "password", password);
        ReflectionUtil.setField(user, "databaseLoginBlocked", databaseLoginBlocked);
        ReflectionUtil.setField(user, "dateOfLastPasswordChange", dateOfLastPasswordChange);
        ReflectionUtil.setField(user, "invalidLoginCount", invalidLoginCount);
        ReflectionUtil.setField(user, "roles", roles);
        ReflectionUtil.setField(user, "sessions", sessions);
        ReflectionUtil.setField(user, "groups", groups);

        user.setEmailFactory(emailFactory);

        return user;
    }

    public UserTestBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserTestBuilder withStatus(Status status) {
        this.status = status;
        return this;
    }

    public UserTestBuilder withInactiveDate(Date inactiveDate) {
        this.inactiveDate = inactiveDate;
        return this;
    }

    public UserTestBuilder withSuccessURL(String successURL) {
        this.successURL = successURL;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestBuilder withChangePasswordOnNextLogon(boolean changePasswordOnNextLogon) {
        this.changePasswordOnNextLogon = changePasswordOnNextLogon;
        return this;
    }

    public UserTestBuilder withPasswordChangeRequired(boolean passwordChangeRequired) {
        this.passwordChangeRequired = passwordChangeRequired;
        return this;
    }

    public UserTestBuilder withDatabaseLoginBlocked(boolean databaseLoginBlocked) {
        this.databaseLoginBlocked = databaseLoginBlocked;
        return this;
    }

    public UserTestBuilder withDateOfLastPasswordChange(Date dateOfLastPasswordChange) {
        this.dateOfLastPasswordChange = dateOfLastPasswordChange;
        return this;
    }

    public UserTestBuilder withInvalidLoginCount(int invalidLoginCount) {
        this.invalidLoginCount = invalidLoginCount;
        return this;
    }

    public UserTestBuilder withLanguage(Language language) {
        this.language = language;
        return this;
    }

    public UserTestBuilder withRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public UserTestBuilder withRoles(Role... roles) {
        this.roles = newHashSet(roles);
        return this;
    }

    public UserTestBuilder withSessions(Set<Session> sessions) {
        this.sessions = sessions;
        return this;
    }

    public UserTestBuilder withGroups(Set<Group> groups) {
        this.groups = groups;
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = emailFactory.email(email);
        return this;
    }

    public UserTestBuilder withEmail(EmailAddress email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withoutEmail() {
        this.email = null;
        return this;
    }
}
