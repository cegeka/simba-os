/*
 * Copyright 2011 Simba Open Source
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
 */
package org.simbasecurity.core.service;

import org.simbasecurity.core.domain.Status;


public interface CredentialService {

    /**
     * Check if a user's user name and password correspond. Uses advanced encryptor.
     *
     * @param username The user name
     * @param password The password
     * @return <code>true</code> if the user name and password correspond; <code>false</code> otherwise
     */
    boolean checkCredentials(String username, String password);

    /**
     * Check if a user's user name and password correspond. Uses the basic encryptor, but only to check.
     *
     * @param username The user name
     * @param password The password
     * @return <code>true</code> if the user name and password correspond; <code>false</code> otherwise
     */
    boolean checkCredentialsWithSHA1EncryptorAndReEncrypt(String username, String password);

    /**
     * Check if a user exists.
     *
     * @param userName the user name
     * @return <code>true</code> if the user exists in the storage; <code>false</code> otherwise
     */
    boolean checkUserExists(String userName);

    /**
     * Increase the number of successive invalid logins for a user and block the user's account if needed.
     *
     * @param username the user name
     * @return <code>true</code> if the account became blocked; <code>false</code> otherwise
     */
    boolean increaseInvalidLoginCountAndBlockAccount(String username);

    /**
     * Resets the number of successive invalid logins for a user.
     *
     * @param userName the user name
     */
    void resetInvalidLoginCount(String userName);

    /**
     * Check if a user has the required status.
     *
     * @param userName       the name of the user to check
     * @param requiredStatus the required status
     * @return <code>true</code> if the user has the required status; <code>false</code> otherwise.
     */
    boolean checkUserStatus(String userName, Status requiredStatus);

    boolean mustChangePasswordOnNextLogon(String userName);

    /**
     * Change password for user
     *
     * @param userName    the user name
     * @param oldPassword Old password to verify the user
     * @param newPassword New password
     * @return True if password change was successful, otherwise false
     */
    boolean changePasswordAuthorized(String userName, String oldPassword, String newPassword);

    /**
     * Change password for user and disable the password expire function.  ONLY to use with the webservices!
     *
     * @param userName    the user name
     * @param oldPassword Old password to verify the user
     * @param newPassword New password
     * @return True if password change was successful, otherwise false
     */
    boolean changePasswordAuthorizedAndDisablePasswordExpire(String userName, String oldPassword, String newPassword);

    /**
     * Change password for user
     *
     * @param userName                the user name
     * @param newPassword             New password
     * @param newPasswordConfirmation Confirmation of the new password. Must be identical to new password.
     */
    void changePassword(String userName, String newPassword, String newPasswordConfirmation);

    String getSuccessURL(String userName);

    /**
     * Loop over all users to check if they didn't change their password for a configurable amount of time.
     * If the uses didn't change his password during this time, the user is marked.
     *
     * @see #mustChangePasswordOnNextLogon(String)
     */
    void markUsersForPasswordChange();

    /**
     * Get the password hash for the user with the specified name.
     *
     * @param username the user name
     * @return the password hash
     */
    String getPasswordHash(String username);
}