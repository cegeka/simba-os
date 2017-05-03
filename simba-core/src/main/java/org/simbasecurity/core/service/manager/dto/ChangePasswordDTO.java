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

public class ChangePasswordDTO extends AbstractVersionedDTO {

	private String userName;

	private String newPassword;
	private String newPasswordConfirmation;

	// filled if the user changes his own password
	private String oldPassword;

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

	@Override
	public String toString() {
		return "ChangePasswordDTO [userName=" + userName + "]";
	}

}