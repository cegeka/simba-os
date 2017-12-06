package org.simbasecurity.core.domain.user;

import org.simbasecurity.common.util.StringUtil;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;

import javax.persistence.Embeddable;

@Embeddable
public class EmailAddress {

    private static final String EMAIL_PATTERN = "^\\w+[\\w_\\.\\-]*@[\\w_\\-]+\\.[\\w_\\.\\-]+$";

    private String email;

    protected EmailAddress() {
    }

    private EmailAddress(String email) {
        this.email = email;
    }

    public static EmailAddress email(String email) {
        if(StringUtil.isEmpty(email)){
            throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_REQUIRED);
        }
        if(!email.matches(EMAIL_PATTERN)) {
            throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_INVALID, String.format("%s is not a valid email address", email));
        }
        return new EmailAddress(email);
    }

    public String asString() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailAddress)) return false;

        EmailAddress that = (EmailAddress) o;

        return email != null ? email.equals(that.email) : that.email == null;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

    @Override
    public String toString() {
        return email;
    }
}
