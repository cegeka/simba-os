package org.simbasecurity.core.domain.user;

import javax.persistence.Embeddable;

@Embeddable
public class EmailAddress {
    private String email;

    protected EmailAddress(){}

    private EmailAddress(String email) {
        this.email = email;
    }

    public static EmailAddress email(String email){
        if(!email.contains("\u0040")){
            throw new RuntimeException(String.format("%s is not a valid email address", email));
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
