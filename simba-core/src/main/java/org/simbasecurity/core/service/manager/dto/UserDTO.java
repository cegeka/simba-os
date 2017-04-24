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

package org.simbasecurity.core.service.manager.dto;

import java.util.Date;

import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.Status;

public class UserDTO extends AbstractVersionedDTO {
    private String userName;
    private String name;
    private String firstName;
    private Date inactiveDate;
    private Status status;
    private String successURL;
    private Language language;
    private boolean mustChangePassword;
    private boolean passwordChangeRequired;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Date getInactiveDate() {
        return inactiveDate;
    }

    public void setInactiveDate(Date inactiveDate) {
        this.inactiveDate = inactiveDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSuccessURL() {
        return successURL;
    }

    public void setSuccessURL(String successURL) {
        this.successURL = successURL;
    }

    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language newLanguage) {
        this.language = newLanguage;
    }

    public boolean isChangePasswordOnNextLogon() {
        return mustChangePassword;
    }

    public void setChangePasswordOnNextLogon(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public void setPasswordChangeRequired(boolean passwordChangeRequired) {
        this.passwordChangeRequired = passwordChangeRequired;
    }

    public boolean isPasswordChangeRequired() {
        return passwordChangeRequired;
    }

    @Override
    public String toString() {
        return "UserDTO [userName=" + userName + "]";
    }
}