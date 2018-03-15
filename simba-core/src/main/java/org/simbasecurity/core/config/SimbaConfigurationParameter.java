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
package org.simbasecurity.core.config;

import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.simbasecurity.core.config.StoreType.DATABASE;
import static org.simbasecurity.core.config.StoreType.QUARTZ;

public enum SimbaConfigurationParameter implements ConfigurationParameter {

    /**
     * Parameter for configuring the application name.
     */
    APPLICATION_NAME(DATABASE, true, StringType.class, "SIMBA Manager"),

    /**
     * Parameter for configuring the life time of a password in days. If a
     * password has been in use for longer than this period, a user should be
     * required to change his password.
     */
    PASSWORD_LIFE_TIME(DATABASE, true, TimeType.class, DAYS, "90"),

    /**
     * Parameter for configuring the maximum successive amount of invalid
     * credentials a user can enter before his account is automatically blocked.
     */
    INVALID_LOGIN_MAX_COUNT(DATABASE, true, IntegerType.class, "5"),

    /**
     * Parameter for configuring a list of possible success url's to be selected
     * by the user.
     */
    SUCCESS_URL(DATABASE, false, StringType.class, "/simba-zoo/index.jsp"),

    /**
     * Parameter for configuring the session time out.
     */
    SESSION_TIME_OUT(DATABASE, true, TimeType.class, MINUTES, "60"),

    /**
     * Parameter for configuring the interval between to session purge runs.
     */
    PURGE_SESSION_INTERVAL(QUARTZ, true, TimeType.class, MINUTES, "60"),

    /**
     * Parameter for configuring the cron expression for marking user required
     * to change their password.
     */
    MARK_PASSWORD_CHANGE_EXPRESSION(QUARTZ, true, StringType.class, ""),
   
    /**
     * Parameter for configuring rule caching.
     */
    CACHING_ENABLED(DATABASE, true, BooleanType.class, "false"),

    /**
     * Parameter for configuring the URL to the login page.
     */
    LOGIN_URL(DATABASE, true, StringType.class, "/jsp/login.jsp"),

    /**
     * Parameter for configuring the URL to the logout page.
     */
    LOGOUT_URL(DATABASE, true, StringType.class, "/jsp/logout.jsp"),

    /**
     * Parameter for configuring the URL to the password changed page.
     */
    PASSWORD_CHANGED_URL(DATABASE, true, StringType.class, "/jsp/changepassword.jsp"),

    /**
     * Parameter for configuring the URL to the password reset page.
     */
    PASSWORD_RESET_URL(DATABASE, true, StringType.class, "/jsp/reset-password.jsp"),

    /**
     * Parameter for configuring the URL's to the password reset page. See issue #15
     */
    PASSWORD_RESET_TOKEN_URL(DATABASE, false, StringType.class, ""),

    PASSWORD_INVALID_TOKEN_URL(DATABASE, true, StringType.class, "/jsp/password-invalid-token.jsp"),

    NEW_PASSWORD_URL(DATABASE, true, StringType.class, "/jsp/new-password.jsp"),

    NEW_PASSWORD_SUCCESS_URL(DATABASE, true, StringType.class, "/jsp/new-password-succes.jsp"),

    /**
     * Parameter for configuring the URL to the access denied page.
     */
    ACCESS_DENIED_URL(DATABASE, true, StringType.class, "/jsp/access-denied.jsp"),

    /**
     * Parameter for configuring the URL to the password reset page.
     */
    CHANGE_PASSWORD_URL(DATABASE, true, StringType.class, "/jsp/changepassword.jsp"),

    /**
     * Parameter for configuring the list of default roles for a user when
     * created via the create user webservice.
     */
    DEFAULT_USER_ROLE(DATABASE, false, StringType.class, "guest"),

    /* VALIDATION PARAMETERS */

    /**
     * Parameter for validating maximum username length.
     */
    USERNAME_MAX_LENGTH(DATABASE, true, IntegerType.class, "999999"),

    /**
     * Parameter for validating minimum username length.
     */
    USERNAME_MIN_LENGTH(DATABASE, true, IntegerType.class, "1"),

    /**
     * Parameter for validating username structure.
     */
    USERNAME_REGEX(DATABASE, true, StringType.class, "\\w*"),

    /**
     * Parameter for validating maximum firstname length.
     */
    FIRSTNAME_MAX_LENGTH(DATABASE, true, IntegerType.class, "20"),

    /**
     * Parameter for validating minimum firstname length.
     */
    FIRSTNAME_MIN_LENGTH(DATABASE, true, IntegerType.class, "1"),

    /**
     * Parameter for validating maximum lastname length.
     */
    LASTNAME_MAX_LENGTH(DATABASE, true, IntegerType.class, "30"),

    /**
     * Parameter for validating minimum lastname length.
     */
    LASTNAME_MIN_LENGTH(DATABASE, true, IntegerType.class, "1"),

    /**
     * Parameter for validating maximum successurl length.
     */
    SUCCESSURL_MAX_LENGTH(DATABASE, true, IntegerType.class, "250"),

    /**
     * Parameter for validating maximum password length.
     */
    PASSWORD_MAX_LENGTH(DATABASE, true, IntegerType.class, "9999"),

    /**
     * Parameter for validating minimum password length.
     */
    PASSWORD_MIN_LENGTH(DATABASE, true, IntegerType.class, "6"),

    /**
     * Parameter for validating the used characters in a password.
     */
    PASSWORD_VALID_CHARACTERS(DATABASE, true, StringType.class, "[\\x21-\\x7E]*"),

    /**
     * Parameter for configuring a list of rules to check the password
     * complexity.
     */
    PASSWORD_COMPLEXITY_RULE(DATABASE, false, StringType.class, ".*[A-Z].*"),

    /**
     * Parameter for configuring the minimum number of matching complexity rules
     * required.
     */
    PASSWORD_MINIMUM_COMPLEXITY(DATABASE, true, IntegerType.class, "3"),

    /**
     * Parameter for configuring if new users should have the password change
     * required enabled.
     */
    PASSWORD_CHANGE_REQUIRED(DATABASE, true, BooleanType.class, "true"),

    /**
     * Parameter for configuring if new users should have a required email.
     */
    EMAIL_ADDRESS_REQUIRED(DATABASE, true, BooleanType.class, "true"),

    /**
     * The name of the JVM argument that points to the Keystore location you
     * want to use. The JVM argument value should be full path on the OS.
     * extension.
     */
    KEYSTORE_JVM_ARG(DATABASE, true, StringType.class, "simbaKeystore"),    // TODO: bkbac: Should this be a system property

    /**
     * Keystore alias for accessing the public and private key.
     */
    KEYSTORE_ALIAS(DATABASE, true, StringType.class, "simbaalias"),
    /**
     * Keystore password used to access the keystore.
     */
    KEYSTORE_PASSWORD(DATABASE, true, StringType.class, "password"),
    /**
     * Enables the audit log integrity mechanism, which will add a digest to
     * your audit trail record.
     */
    @Deprecated
    AUDIT_LOG_INTEGRITY_ENABLED(DATABASE, true, BooleanType.class, "true"),
    /**
     * Parameter for configuring the maximum elapsed time between opening the login page and the actual login.
     */
    MAX_LOGIN_ELAPSED_TIME(DATABASE, true, TimeType.class, MINUTES, "2"),
    /**
     * How much time a ResetPasswordUserToken used in the reset password mechanism expires.
     */
    RESET_PASSWORD_USERTOKEN_EXPIRATION_TIME(DATABASE, true, TimeType.class, MINUTES, "10"),
    /**
     * How much time a UserCreationUserToken used in the user creation mechanism expires.
     */
    USER_CREATION_USERTOKEN_EXPIRATION_TIME(DATABASE, true, TimeType.class, DAYS, "15"),

    MAIL_SERVER_HOST_NAME(StoreType.DATABASE, true, StringType.class, ""),
    MAIL_SERVER_PORT(StoreType.DATABASE, true, IntegerType.class, "0"),
    
    EXPIRED_URL(StoreType.DATABASE, true, StringType.class, "/jsp/expired.jsp"),

    ENABLE_AD_GROUPS(StoreType.DATABASE, true, BooleanType.class, "false"),

    ADMIN_ROLE_NAME(StoreType.DATABASE, true, StringType.class, "simba-admin"),

    SAML_IDP_SSO_TARGET_URL(StoreType.DATABASE, true, StringType.class, ""),
    SAML_IDP_SLO_TARGET_URL(StoreType.DATABASE, true, StringType.class, ""),
    SAML_ASSERTION_CONSUMER_SERVICE_URL(StoreType.DATABASE, true, StringType.class, ""),
    SAML_ISSUER(StoreType.DATABASE, true, StringType.class, ""),
    SAML_IDP_CERTIFICATE(DATABASE, true, StringType.class, ""),
    ;

    private final ConfigurationHelper helper;
    private final ChronoUnit timeUnit;

    SimbaConfigurationParameter(StoreType storeType, boolean unique, Class<? extends Type<?>> type, String defaultValue) {
        this(storeType, unique, type, null, defaultValue);
    }

    SimbaConfigurationParameter(StoreType storeType, boolean unique, Class<? extends Type<?>> type, ChronoUnit timeUnit, String defaultValue) {
        try {
            this.helper = new ConfigurationHelper(name(), storeType, unique, type, defaultValue);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        if (type.equals(TimeType.class) && timeUnit == null) {
            throw new IllegalArgumentException("Parameter of time type requires a time unit");
        }
        this.timeUnit = timeUnit;
    }

    /**
     * @return the chrono unit to use for a {@link org.simbasecurity.core.config.TimeType time} parameter
     * @see org.simbasecurity.core.config.TimeType
     */
    public ChronoUnit getChronoUnit() {
        return timeUnit;
    }

    public ConfigurationHelper helper() {
        return helper;
    }
}
