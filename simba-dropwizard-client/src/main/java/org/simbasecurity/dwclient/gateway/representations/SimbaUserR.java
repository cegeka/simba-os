package org.simbasecurity.dwclient.gateway.representations;

import java.util.Date;

import org.simbasecurity.dwclient.gateway.protocol.ESAPIDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class SimbaUserR {

	private long id;
	private int version;
	@JsonDeserialize(using = ESAPIDeserializer.class)
	private String userName;
	@JsonDeserialize(using = ESAPIDeserializer.class)
	private String name;
	@JsonDeserialize(using = ESAPIDeserializer.class)
	private String firstName;
	private Date inactiveDate;
	@JsonDeserialize(using = ESAPIDeserializer.class)
	private String status;
	@JsonDeserialize(using = ESAPIDeserializer.class)
	private String successURL;
	@JsonDeserialize(using = ESAPIDeserializer.class)
	private String language;
	private boolean passwordChangeRequired = false;
	private boolean changePasswordOnNextLogon = false;

	// necessary for jackson
	public SimbaUserR() {
	}

	public SimbaUserR(String userName) {
		this.userName = userName;
		this.language = "nl_NL";
		this.status = "ACTIVE";
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

	public Date getInactiveDate() {
		return inactiveDate;
	}

	public void setInactiveDate(Date inactiveDate) {
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
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isPasswordChangeRequired() {
		return passwordChangeRequired;
	}

	public void setPasswordChangeRequired(boolean passwordChangeRequired) {
		this.passwordChangeRequired = passwordChangeRequired;
	}

	public boolean isChangePasswordOnNextLogon() {
		return changePasswordOnNextLogon;
	}

	public void setChangePasswordOnNextLogon(boolean changePasswordOnNextLogon) {
		this.changePasswordOnNextLogon = changePasswordOnNextLogon;
	}

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

}
