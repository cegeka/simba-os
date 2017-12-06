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
package org.simbasecurity.core.exception;

public enum SimbaMessageKey {
    EMPTY_USERNAME,
    EMPTY_PASSWORD,
    WRONG_PASSWORD,
    EMPTY_TARGET_URL,
    LOGIN_FAILED,
    ACCESS_DENIED,
    EMPTY_SUCCESS_URL,
    PASSWORD_INVALID_LENGTH,
    PASSWORD_INVALID_COMPLEXITY,
    PASSWORDS_DONT_MATCH,
    PASSWORD_SAME_AS_OLD,
    ACCOUNT_BLOCKED,
    OPTIMISTIC_LOCK,
    INVALID_START_CONDITION,
    INVALID_END_CONDITION,

    USERNAME_EMPTY,
    USERNAME_TOO_SHORT,
    USERNAME_TOO_LONG,
    USERNAME_INVALID,
    FIRSTNAME_TOO_SHORT,
    FIRSTNAME_TOO_LONG,
    NAME_TOO_SHORT,
    NAME_TOO_LONG,
    SUCCESSURL_TOO_LONG,
    LANGUAGE_EMPTY,
    STATUS_EMPTY,
    USER_ALREADY_EXISTS,
    USER_DOESNT_EXISTS,
    EMAIL_ADDRESS_REQUIRED,
    EMAIL_ADDRESS_INVALID,

    LOGIN_TIME_EXPIRED,
    MAIL_ERROR
}
