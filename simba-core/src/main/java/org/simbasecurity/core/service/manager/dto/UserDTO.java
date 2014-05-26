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