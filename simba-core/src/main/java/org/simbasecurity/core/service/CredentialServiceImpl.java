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

import static org.simbasecurity.core.exception.SimbaMessageKey.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.exception.SimbaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(noRollbackFor = EncryptionOperationNotPossibleException.class)
public class CredentialServiceImpl implements CredentialService {

    @Autowired private ConfigurationService configurationService;
    @Autowired private UserRepository userRepository;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogEventFactory;

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
    public void resetInvalidLoginCount(String userName) {
        User user = findUser(userName);
        user.resetInvalidLoginCount();
    }

    @Override
    public boolean increaseInvalidLoginCountAndBlockAccount(String username) {
        User user = userRepository.findByName(username);
        boolean accountBlocked = false;
        if (user != null) {
            int invalidLoginCount = user.increaseInvalidLoginCount();
            Integer maxInvalidLoginCount = configurationService.getValue(ConfigurationParameter.INVALID_LOGIN_MAX_COUNT);
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

        int passwordLifeTime = (Integer) configurationService.getValue(ConfigurationParameter.PASSWORD_LIFE_TIME);

        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

        for (User user : allUsers) {
            Date changeDate = user.getDateOfLastPasswordChange();
            Date lastDateForNextChange = DateUtils.addDays(changeDate, passwordLifeTime);

            if (user.isPasswordChangeRequired() && today.after(lastDateForNextChange)) {
                user.setChangePasswordOnNextLogon(true);
            }
        }
    }

    @Override
    public boolean changePasswordAuthorized(String userName, String oldPassword, String newPassword) {
        User user = findUser(userName);
        return user.changePasswordAuthorized(oldPassword, newPassword);
    }

    @Override
    public boolean changePasswordAuthorizedAndDisablePasswordExpire(String userName, String oldPassword, String newPassword) {
        User user = findUser(userName);
        user.setPasswordChangeRequired(false);
        user.setChangePasswordOnNextLogon(false);
        return user.changePasswordAuthorized(oldPassword, newPassword);
    }

    @Override
    @Transactional(noRollbackFor = SimbaException.class)
    public void changePassword(String userName, String newPassword, String newPasswordConfirmation) {
        User user = findUser(userName);
        user.changePassword(newPassword, newPasswordConfirmation);
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

    private User findUser(String userName) {
        User user = userRepository.findByName(userName);

        if (user == null) {
        	audit.log(auditLogEventFactory.createEventForUserPasswordForm(userName, USER_DOESNT_EXISTS.name()));
        	throw new SimbaException(USER_DOESNT_EXISTS, userName);
        }
        return user;
    }

}