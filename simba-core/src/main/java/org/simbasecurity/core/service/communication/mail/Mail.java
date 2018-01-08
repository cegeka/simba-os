package org.simbasecurity.core.service.communication.mail;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.simbasecurity.core.domain.user.EmailAddress;

import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;

public class Mail {
    private EmailAddress from;
    private EmailAddress to;
    private String subject;
    private String body;

    public static Mail mail() {
        return new Mail();
    }

    public Mail body(String body) {
        this.body = body;
        return this;
    }

    public Mail to(EmailAddress to) {
        this.to = to;
        return this;
    }

    public Mail from(EmailAddress from) {
        this.from = from;
        return this;
    }

    public Mail subject(String subject) {
        this.subject = subject;
        return this;
    }

    public EmailAddress getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }

    public EmailAddress getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    @Override
    public boolean equals(Object object){
        return EqualsBuilder.reflectionEquals(object, this);
    }

    @Override
    public int hashCode(){
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
