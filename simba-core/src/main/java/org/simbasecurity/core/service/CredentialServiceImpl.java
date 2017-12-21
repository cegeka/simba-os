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
package org.simbasecurity.core.service;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.password.PasswordEncryptor;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.audit.ManagementAudit;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static org.simbasecurity.core.exception.SimbaMessageKey.USER_DOESNT_EXISTS;

@Service
@Transactional(noRollbackFor = EncryptionOperationNotPossibleException.class)
public class CredentialServiceImpl implements CredentialService {

    @Autowired private CoreConfigurationService configurationService;
    @Autowired private UserRepository userRepository;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogEventFactory;
    @Autowired private ManagementAudit managementAudit;

    @Override
    public boolean checkCredentials(String username, String password) {
        User user = userRepository.findByName(username);
        return user != null && !user.isDatabaseLoginBlocked() && checkPassword(password,user);
    }
    
	private boolean checkPassword(String password, User user) {
		boolean result = user.checkPassword(password);
		if(!result){
			audit.log(auditLogEventFactory.createEventForUserPasswordForm(user.getUserName(), AuditMessages.WRONG_PASSWORD));
		}
		return result;
	}

    @Override
    public boolean checkCredentialsWithSHA1EncryptorAndReEncrypt(String username, String password) {
        User user = userRepository.findByName(username);
        return user != null && !user.isDatabaseLoginBlocked() && user.checkPasswordWithSHA1EncryptorAndReEncrypt(password);
    }

    @Override
    public boolean checkCredentials(String username, String password, PasswordEncryptor encryptor, boolean reEncrypt) {
        User user = userRepository.findByName(username);
        return user != null && !user.isDatabaseLoginBlocked() && user.checkPassword(password, encryptor, reEncrypt);
    }

    @Override
    public void resetInvalidLoginCount(String userName) {
        User user = findUser(userName);
        user.resetInvalidLoginCount();

        managementAudit.log("Invalid login count resetted for user ''{0}''", userName);
    }

    @Override
    public boolean increaseInvalidLoginCountAndBlockAccount(String username) {
        User user = userRepository.findByName(username);
        boolean accountBlocked = false;
        if (user != null) {
            int invalidLoginCount = user.increaseInvalidLoginCount();
            Integer maxInvalidLoginCount = configurationService.getValue(SimbaConfigurationParameter.INVALID_LOGIN_MAX_COUNT);
            if (invalidLoginCount >= maxInvalidLoginCount) {
                user.setStatus(Status.BLOCKED);
                accountBlocked = true;
            }
        }
        return accountBlocked;
    }

    @Override
    public boolean checkUserExists(String userName) {
        User user = userRepository.findByName(userName);
        return user != null;
    }

    @Override
    public boolean checkUserStatus(String userName, Status requiredStatus) {
        User user = findUser(userName);
        return requiredStatus.equals(user.getStatus());
    }

    @Override
    public boolean mustChangePasswordOnNextLogon(String userName) {
        User user = findUser(userName);
        return user.isChangePasswordOnNextLogon();
    }

    @Override
    public void markUsersForPasswordChange() {
        Collection<User> allUsers = userRepository.findAll();

        long passwordLifeTime = configurationService.getValue(SimbaConfigurationParameter.PASSWORD_LIFE_TIME);


        for (User user : allUsers) {

            LocalDateTime changeDate;
            if (user.getDateOfLastPasswordChange() != null) {
                changeDate = LocalDateTime.ofInstant(user.getDateOfLastPasswordChange().toInstant(), ZoneId.systemDefault());
            } else {
                changeDate = LocalDateTime.ofInstant(new Date(0).toInstant(), ZoneId.systemDefault());
            }

            LocalDateTime lastDateForNextChange = changeDate.plus(passwordLifeTime, SimbaConfigurationParameter.PASSWORD_LIFE_TIME.getChronoUnit());

            if (user.isPasswordChangeRequired() && LocalDate.now().isAfter(lastDateForNextChange.toLocalDate())) {
                user.setChangePasswordOnNextLogon(true);
            }
        }
    }

    @Override
    public boolean changePasswordAuthorized(String userName, String oldPassword, String newPassword) {
        User user = findUser(userName);

        managementAudit.log("Password changed for user ''{0}''", userName);

        return user.changePasswordAuthorized(oldPassword, newPassword);
    }

    @Override
    public boolean changePasswordAuthorizedAndDisablePasswordExpire(String userName, String oldPassword, String newPassword) {
        User user = findUser(userName);
        user.setPasswordChangeRequired(false);
        user.setChangePasswordOnNextLogon(false);

        managementAudit.log("Password changed for user ''{0}''. Password expire disabled.", userName);

        return user.changePasswordAuthorized(oldPassword, newPassword);
    }

    @Override
    @Transactional(noRollbackFor = SimbaException.class)
    public void changePassword(String userName, String newPassword, String newPasswordConfirmation) {
        User user = findUser(userName);
        user.changePassword(newPassword, newPasswordConfirmation);

        managementAudit.log("Password changed for user ''{0}''", userName);
    }

    @Override
    public String getSuccessURL(String userName) {
        User user = findUser(userName);
        return user.getSuccessURL();
    }

    @Override
    public String getPasswordHash(String username) {
        return findUser(username).getPasswordHash();
    }

    @Override
    public Optional<User> findUserByMail(EmailAddress email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    private User findUser(String userName) {
        User user = userRepository.findByName(userName);

        if (user == null) {
        	audit.log(auditLogEventFactory.createEventForUserPasswordForm(userName, USER_DOESNT_EXISTS.name()));
        	throw new SimbaException(USER_DOESNT_EXISTS, userName);
        }
        return user;
    }

}