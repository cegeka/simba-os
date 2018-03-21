package org.simbasecurity.core.domain.user;

import javax.persistence.Embeddable;

@Embeddable
public class EmailAddress {

    private String email;

    protected EmailAddress() {
    }

    EmailAddress(String email) {
        this();
        this.email = email;
    }

    public String asString() {
        return email;
    }

    public EmailAddress getLowerCaseEmailAddress() {
        return new EmailAddress(email == null ? null : email.toLowerCase());
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
