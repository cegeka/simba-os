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

package org.simbasecurity.manager.service.rest.dto;

public class UserDTO  {

    private long id;
    private int version;
    private String userName;
    private String name;
    private String firstName;
    private String inactiveDate;
    private String status;
    private String successURL;
    private String language;
    private boolean mustChangePassword;
    private boolean passwordChangeRequired;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

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

    public String getInactiveDate() {
        return inactiveDate;
    }

    public void setInactiveDate(String inactiveDate) {
        this.inactiveDate = inactiveDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSuccessURL() {
        return successURL;
    }

    public void setSuccessURL(String successURL) {
        this.successURL = successURL;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String newLanguage) {
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