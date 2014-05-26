package org.simbasecurity.core.service.manager.dto;

public class ChangePasswordDTO extends AbstractVersionedDTO {

	private String userName;

	private String newPassword;
	private String newPasswordConfirmation;

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(final String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordConfirmation() {
		return newPasswordConfirmation;
	}

	public void setNewPasswordConfirmation(final String newPasswordConfirmation) {
		this.newPasswordConfirmation = newPasswordConfirmation;
	}

	@Override
	public String toString() {
		return "ChangePasswordDTO [userName=" + userName + "]";
	}

}