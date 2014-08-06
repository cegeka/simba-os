package org.simbasecurity.dwclient.domain.user;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.jasypt.util.password.PasswordEncryptor;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.util.PasswordEncryptorFactory;

import com.google.common.base.Objects;

@Entity
@Table(name = "SIMBA_USER")
public class SimbaUser implements Serializable {
	private static final long serialVersionUID = -5502374573813758067L;

	@Version
	private int version;

	public int getVersion() {
		return version;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@SuppressWarnings("unchecked")
	private static final ObjectPool<PasswordEncryptor> ENCRYPTOR_POOL = new StackObjectPool<PasswordEncryptor>(new PasswordEncryptorFactory(), 2, 2);

	private static PasswordEncryptor retrievePasswordEncryptor() {
		try {
			return ENCRYPTOR_POOL.borrowObject();
		} catch (Exception e) {
			throw new RuntimeException("Unable to borrow buffer from pool" + e.toString());
		}
	}

	private static void returnPasswordEncryptor(PasswordEncryptor encryptor) {
		try {
			ENCRYPTOR_POOL.returnObject(encryptor);
		} catch (Exception ignore) {
		}
	}

	@SuppressWarnings("unused")
	private SimbaUser() {
	}

	public SimbaUser(String emailAddress, String password) {
		this.userName = emailAddress;
		this.changePasswordOnNextLogon = false;
		this.databaseLoginBlocked = false;
		this.language = Language.en_US;
		this.status = Status.ACTIVE;
		setPassword(password);
	}

	public void setPassword(String password) {
		PasswordEncryptor encryptor = retrievePasswordEncryptor();
		try {
			this.password = encryptor.encryptPassword(password);
		} finally {
			returnPasswordEncryptor(encryptor);
		}
		this.dateOfLastPasswordChange = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public Language getLanguage() {
		return language;
	}

	public Status getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(userName, password);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimbaUser other = (SimbaUser) obj;
		return Objects.equal(userName, other.userName)
				&& Objects.equal(password, other.password);
	}

}
