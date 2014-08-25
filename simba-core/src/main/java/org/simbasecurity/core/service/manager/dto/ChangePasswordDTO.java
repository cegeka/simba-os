package org.simbasecurity.core.service.manager.dto;

public class ChangePasswordDTO extends AbstractVersionedDTO {

	private String userName;

	private String newPassword;
	private String newPasswordConfirmation;

	// filled if the user changes his own password
	private String oldPassword;
	// filled if the admin changes the password
	private String ssoToken;

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

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(final String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getSsoToken() {
		return ssoToken;
	}

	public void setSsoToken(final String ssoToken) {
		this.ssoToken = ssoToken;
	}

	@Override
	public String toString() {
		return "ChangePasswordDTO [userName=" + userName + "]";
	}

}