package org.simbasecurity.core.domain.user;

import org.apache.commons.lang.StringUtils;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.service.config.CoreConfigurationService;

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
        if(!isEmailRequired() && isBlankOrEmpty(email)){
            return EmailAddress.emptyEmail();
        }
        if(isBlankOrEmpty(email)){
            throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_REQUIRED);
        }
        if(!email.matches(EMAIL_PATTERN)) {
            throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_INVALID, String.format("%s is not a valid email address", email));
        }
        return new EmailAddress(email);
    }

    public static EmailAddress emptyEmail() {
        return new EmailAddress(null);
    }

    public static EmailAddress orEmpty(EmailAddress email) {
        return email != null ? email : EmailAddress.emptyEmail();
    }

    private static boolean isBlankOrEmpty(String email) {
        return StringUtils.isEmpty(email) || StringUtils.isBlank(email);
    }

    private static boolean isEmailRequired() {
        CoreConfigurationService configurationService = GlobalContext.locate(CoreConfigurationService.class);
        return configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED);
    }

    public String asString() {
        return email;
    }

    public boolean isEmpty() {
        return email == null;
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

    public static String nullSafeAsString(EmailAddress emailAddress) {
        return emailAddress != null ? emailAddress.asString() : null;
    }
}
