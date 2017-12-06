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

import org.jasypt.util.password.PasswordEncryptor;
import org.simbasecurity.core.domain.user.EmailAddress;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * @since 1.0
 */
public interface User extends Versionable {

	/**
	 * @return the unique user name for the user
	 */
	String getUserName();

	/**
	 * @return the user's name
	 */
	String getName();

	/**
	 * @param name
	 *            sets the user's name
	 */
	void setName(String name);

	/**
	 * @return the user's first name
	 */
	String getFirstName();

	/**
	 * @param firstName
	 *            sets the user's first name
	 */
	void setFirstName(String firstName);

	/**
	 * @return the current status of the user
	 */
	Status getStatus();

	/**
	 * @param status
	 *            sets a new status for the user
	 */
	void setStatus(Status status);

	/**
	 * @return the date at which the user has become inactive. If the user is
	 *         still active, this method returns <tt>null<tt>.
	 * @see #getStatus()
	 * @see org.simbasecurity.core.domain.Status#INACTIVE
	 */
	Date getInactiveDate();

	/**
	 * @return the URL to redirect the user to on successful log on.
	 */
	String getSuccessURL();

	/**
	 * @param successURL
	 *            sets a new success URL for the use
	 */
	void setSuccessURL(String successURL);

	/**
	 * @return the users Language
	 */
	Language getLanguage();

	/**
	 * @param language
	 *            sets a new language for the user
	 */
	void setLanguage(Language language);

	/**
	 * @return the current {@link org.simbasecurity.core.domain.Session} for the
	 *         user. If there is not current
	 *         {@link org.simbasecurity.core.domain.Session} this method should
	 *         return <tt>null</tt>.
	 */
	Set<Session> getSessions();

	/**
	 * @return the collection of {@link Role roles} for the user.
	 */
	Set<Role> getRoles();

	void addRoles(Collection<Role> newRoles);

	/**
	 * @param role
	 *            add a new {@link Role role} for the user
	 */
	void addRole(Role role);

	/**
	 * @param role
	 *            remove a specific {@link Role role} from the user
	 */
	void removeRole(Role role);

	/**
	 * @param password
	 *            the plain text password to check
	 * @return <tt>true</tt> if the given password matches the stored password
	 *         for this user; <tt>false</tt> otherwise
	 */
	boolean checkPassword(String password);

	/**
	 * @param password
	 *            the plain text password to check according to the SHA1
	 *            encryptor. Should only be used for legacy systems using SHA-1.
	 * @return <tt>true</tt> if the given password matches the stored password
	 *         for this user; <tt>false</tt> otherwise
	 */
	boolean checkPasswordWithSHA1EncryptorAndReEncrypt(String password);


	/**
	 * This methods allows for checking the password against any provided encryptor. This is useful for
	 * providing a fallback login module whenever the hashing algorithm internally changes.
	 *
	 * @param plainPassword the plain text password to check according to the provided encryptor
	 * @param encryptor     the encryptor to use for checking the password
	 * @param reEncrypt     whether to re-encrypt the stored password to the standard mechanism; or not
	 * @return <tt>true</tt> if the given password matches the stored password
	 *         for this user; <tt>false</tt> otherwise
	 */
	boolean checkPassword(String plainPassword, PasswordEncryptor encryptor, boolean reEncrypt);

	/**
	 * Changes the password to newPassword if oldPassword is valid for this
	 * user.
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @return true if oldpassword is valid for this user and new password has
	 *         successfully been set
	 */
	boolean changePasswordAuthorized(String oldPassword, String newPassword);

	/**
	 * Changes the password to newPassword.
	 * 
	 * @param newPassword
	 * @param newPasswordConfirmation
	 *            Confirmation of the new password. Must be identical to new
	 *            password.
	 */
	void changePassword(String newPassword, String newPasswordConfirmation);

	/**
	 * Reset password to default
	 */
	void resetPassword();

	/**
	 * Checks if the user is required to change his password at next login.
	 * <p/>
	 * A user is required to change his password if the password has been used
	 * for longer then a certain amount of days, or if an administrator
	 * explicitly flags the user as required to change his password.
	 * 
	 * @return <tt>true</tt> if the user is required to change his password;
	 *         <tt>false</tt> otherwise
	 * @see #setChangePasswordOnNextLogon(boolean)
	 */
	boolean isChangePasswordOnNextLogon();

	void setChangePasswordOnNextLogon(boolean mustChangePassword);

	/**
	 * @return <tt>true</tt> if this user is not required to change his
	 *         password; <tt>false</tt> otherwise
	 * @see #setPasswordChangeRequired(boolean)
	 */
	boolean isPasswordChangeRequired();

	/**
	 * Sets whether the user is not required to change his password at a
	 * configured interval.
	 * 
	 * @param passwordChangeNotRequired
	 *            the policy
	 */
	void setPasswordChangeRequired(boolean passwordChangeNotRequired);

	/**
	 * @return the date when the user last changed his password
	 */
	Date getDateOfLastPasswordChange();

	/**
	 * Resets the user's invalid login counter to zero.
	 */
	void resetInvalidLoginCount();

	/**
	 * Increase the number of successive invalid logins by the user with 1.
	 * 
	 * @return the new number of successive invalid logins performed by the
	 *         user.
	 */
	int increaseInvalidLoginCount();

	/**
	 * Checks if the user is allowed to login using the credentials stored in
	 * the database or not.
	 * <p/>
	 * This is useful if multiple JAAS login modules are specified. The database
	 * always contains a user entry, but we don't want a use to have multiple
	 * credentials.
	 * 
	 * @return <code>true</code> if database login is blocked;
	 *         <code>false</code> otherwise.
	 * @see #setDatabaseLoginBlocked(boolean)
	 */
	boolean isDatabaseLoginBlocked();

	/**
	 * @return the password hash
	 */
	String getPasswordHash();

	void clearGroups();

	Collection<Group> getGroups();

	void addGroup(Group group);

	boolean hasRole(String roleName);

    EmailAddress getEmail();

    void setEmail(EmailAddress email);
}
