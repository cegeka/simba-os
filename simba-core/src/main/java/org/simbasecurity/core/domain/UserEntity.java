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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.util.PasswordEncryptorFactory;
import org.simbasecurity.core.util.SHA1PasswordEncryptorFactory;

import javax.persistence.*;
import java.util.*;

import static org.simbasecurity.core.domain.Status.INACTIVE;
import static org.simbasecurity.core.exception.SimbaMessageKey.PASSWORDS_DONT_MATCH;
import static org.simbasecurity.core.exception.SimbaMessageKey.PASSWORD_SAME_AS_OLD;

@Entity
@Table(name = "SIMBA_USER")
public class UserEntity extends AbstractVersionedEntity implements User {

	private static final long serialVersionUID = 552484022516217422L;

	private static final ObjectPool<ConfigurablePasswordEncryptor> ENCRYPTOR_POOL = new StackObjectPool<ConfigurablePasswordEncryptor>(
			new PasswordEncryptorFactory(), 2, 2);

	private static ConfigurablePasswordEncryptor retrievePasswordEncryptor() {
		try {
			return ENCRYPTOR_POOL.borrowObject();
		} catch (Exception e) {
			throw new RuntimeException("Unable to borrow buffer from pool" + e.toString());
		}
	}

	private static void returnPasswordEncryptor(ConfigurablePasswordEncryptor encryptor) {
		try {
			ENCRYPTOR_POOL.returnObject(encryptor);
		} catch (Exception ignore) {
		}
	}

	@Id
	@GeneratedValue(generator = "simbaSequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_USER", allocationSize = 1)
	protected long id = 0;

	@Column(unique = true)
	private String userName;
	@Column(name = "NAME")
	private String name;
	@Column(name = "FIRSTNAME")
	private String firstName;

	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(name = "INACTIVEDATE")
	private Date inactiveDate;
	@Column(name = "SUCCESSURL")
	private String successURL;

	@Column(name = "PASSWORD", nullable = false)
	private String password;
	@Column(name = "CHANGEPASSWORDONNEXTLOGON")
	private boolean changePasswordOnNextLogon;
	@Column(name = "PASSWORDCHANGEREQUIRED")
	private boolean passwordChangeRequired;
	@Column(name = "DATABASELOGINBLOCKED")
	private boolean databaseLoginBlocked;
	@Column(name = "DATEOFLASTPASSWORDCHANGE")
	private Date dateOfLastPasswordChange;

	@Column(name = "INVALIDLOGINCOUNT")
	private int invalidLoginCount;

	@Enumerated(EnumType.STRING)
	private Language language;

	@Embedded
	private EmailAddress email;

	@ManyToMany(targetEntity = RoleEntity.class)
	@JoinTable(name = "SIMBA_USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
	@OrderBy("name")
	private Set<Role> roles = new HashSet<Role>();

	@OneToMany(targetEntity = SessionEntity.class, mappedBy = "user")
	private Set<Session> sessions = new HashSet<Session>();

	@ManyToMany(targetEntity = GroupEntity.class)
	@JoinTable(name = "SIMBA_USER_GROUP", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "GROUP_ID"))
	@OrderBy("name")
	private Set<Group> groups = new HashSet<Group>();

	protected UserEntity() {
	}

	public static UserEntity eidUser(String userName, String firstName, String name, Language language) {
		return new UserEntity(userName, firstName, name, null, language, Status.ACTIVE, false, false, null);
	}

	public static UserEntity restUser(String userName, String firstName, String name, String successURL, Language language, Status status,
									  boolean changePasswordOnNextLogon, boolean passwordChangeRequired){
		return new UserEntity(userName, firstName, name, successURL, language, status, changePasswordOnNextLogon, passwordChangeRequired, null);
	}

	public static UserEntity user(String userName, String firstName, String name, String successURL, Language language, Status status,
								  boolean changePasswordOnNextLogon, boolean passwordChangeRequired, EmailAddress email) {
		return new UserEntity(userName, firstName, name, successURL, language, status, changePasswordOnNextLogon, passwordChangeRequired, email);
	}

	private UserEntity(String userName, String firstName, String name, String successURL, Language language, Status status,
			boolean changePasswordOnNextLogon, boolean passwordChangeRequired, EmailAddress email) {
		setUserName(userName);
		setFirstName(firstName);
		setName(name);
		setSuccessURL(successURL);
		setLanguage(language);
		setStatus(status);
		setChangePasswordOnNextLogon(changePasswordOnNextLogon);
		setPasswordChangeRequired(passwordChangeRequired);
		setEmail(email);
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	private void setUserName(String userName) {
		getUserValidator().validateUserName(userName);
		this.userName = userName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		getUserValidator().validateName(name);
		this.name = name;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		getUserValidator().validateFirstName(firstName);
		this.firstName = firstName;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void setStatus(Status status) {
		getUserValidator().validateStatus(status);
		setInactiveDate(status);
		this.status = status;
	}

	@Override
	public Date getInactiveDate() {
		return inactiveDate;
	}

	private void setInactiveDate(Status status) {
		if (this.status != status) {
			this.inactiveDate = INACTIVE.equals(status) ? DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH) : null;
		}
	}

	@Override
	public String getSuccessURL() {
		return successURL;
	}

	@Override
	public void setSuccessURL(String successURL) {
		getUserValidator().validateSuccessURL(successURL);
		this.successURL = successURL;
	}

	@Override
	public Set<Role> getRoles() {
		return roles;
	}

	@Override
	public boolean hasRole(String roleName) {
		if (roleName == null) {
			return false;
		}

		for (Role role : roles) {
			if (role.getName().equals(roleName.trim())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void addRoles(Collection<Role> newRoles) {
		for (Role role : newRoles) {
			addRole(role);
		}
	}

	@Override
	public void addRole(Role role) {
		roles.add(role);
		role.addUser(this);
	}

	@Override
	public void removeRole(Role role) {
		roles.remove(role);
		role.removeUser(this);
	}

	@Override
	public Set<Session> getSessions() {
		return sessions;
	}

	@Override
	public boolean checkPassword(String password) {
		ConfigurablePasswordEncryptor encryptor = retrievePasswordEncryptor();
		try {
			return checkPassword(password, encryptor, false);
		} finally {
			returnPasswordEncryptor(encryptor);
		}
	}

	@Override
	public boolean checkPasswordWithSHA1EncryptorAndReEncrypt(String plainPassword) {
		return checkPassword(plainPassword, new SHA1PasswordEncryptorFactory().createLegacyEncryptor(), true);
	}

	public boolean checkPassword(String plainPassword, PasswordEncryptor encryptor, boolean reEncrypt) {
		boolean validPassword = encryptor.checkPassword(plainPassword, this.password);
		if (validPassword && reEncrypt) {
			reEncryptPassword(plainPassword);
		}
		return validPassword;
	}

	private void reEncryptPassword(String plainPassword) {
		ConfigurablePasswordEncryptor encryptor = retrievePasswordEncryptor();
		try {
			this.password = encryptor.encryptPassword(plainPassword);
		} finally {
			returnPasswordEncryptor(encryptor);
		}
	}

	@Override
	public boolean changePasswordAuthorized(String oldPassword, String newPassword) {
		boolean result = false;

		if (checkPassword(oldPassword)) {
			changePassword(newPassword);
			result = true;
		}

		return result;
	}

	@Override
	public void changePassword(String newPassword, String newPasswordConfirmation) {
		if (newPassword == null) {
			throw new IllegalArgumentException("New password should not be null");
		}

		if (!newPassword.equals(newPasswordConfirmation)) {
			throw new SimbaException(PASSWORDS_DONT_MATCH);
		}

		changePassword(newPassword);
	}

	private void changePassword(String newPassword) {
		boolean isNewPasswordSameAsOld = false;
		try {
			isNewPasswordSameAsOld = this.password != null && !this.password.isEmpty() && checkPassword(newPassword);
		} catch (EncryptionOperationNotPossibleException ignore) {
			// Stored password is not encrypted using Jasypt. Presume it is not
			// the same.
		}

		if (isNewPasswordSameAsOld) {
			throw new SimbaException(PASSWORD_SAME_AS_OLD);
		}
		setPassword(newPassword);
		setChangePasswordOnNextLogon(false);
	}

	private void setPassword(String newPassword) {
		getPasswordValidator().validatePassword(newPassword);

		ConfigurablePasswordEncryptor encryptor = retrievePasswordEncryptor();
		try {
			this.password = encryptor.encryptPassword(newPassword);
		} finally {
			returnPasswordEncryptor(encryptor);
		}

		this.dateOfLastPasswordChange = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
	}

	@Override
	public boolean isChangePasswordOnNextLogon() {
		return changePasswordOnNextLogon;
	}

	@Override
	public Language getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(Language language) {
		getUserValidator().validateLanguage(language);
		this.language = language;
	}

	@Override
	public void setChangePasswordOnNextLogon(boolean changePasswordOnNextLogon) {
		this.changePasswordOnNextLogon = changePasswordOnNextLogon;
	}

	@Override
	public boolean isPasswordChangeRequired() {
		return passwordChangeRequired;
	}

	@Override
	public void setPasswordChangeRequired(boolean passwordChangeRequired) {
		this.passwordChangeRequired = passwordChangeRequired;
	}

	@Override
	public Date getDateOfLastPasswordChange() {
		return dateOfLastPasswordChange;
	}

	@Override
	public int increaseInvalidLoginCount() {
		return ++invalidLoginCount;
	}

	@Override
	public void resetInvalidLoginCount() {
		invalidLoginCount = 0;
	}

	@Override
	public boolean isDatabaseLoginBlocked() {
		return databaseLoginBlocked;
	}

	@Override
	public String getPasswordHash() {
		return password;
	}

	@Override
	public void clearGroups() {
		groups.clear();
	}

	@Override
	public Collection<Group> getGroups() {
		return Collections.unmodifiableCollection(groups);
	}

	@Override
	public void addGroup(Group group) {
		groups.add(group);
		((GroupEntity) group).addUser(this);
	}

	private UserValidator getUserValidator() {
		return GlobalContext.locate(UserValidator.class);
	}

	private PasswordValidator getPasswordValidator() {
		return GlobalContext.locate(PasswordValidator.class);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UserEntity)) {
			return false;
		}
		UserEntity ue = (UserEntity) o;
		return new EqualsBuilder().append(id, ue.id).append(userName, ue.userName).append(name, ue.name).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(name).append(userName).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id).append("userName", userName).toString();
	}

	public void setEmail(EmailAddress email) {
        this.email = email;
    }

    @Override
    public EmailAddress getEmail() {
        return email;
    }
}