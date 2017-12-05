package org.simbasecurity.core.service.communication.mail;

import org.simbasecurity.core.domain.user.EmailAddress;

import java.net.URL;

public interface MailService {

    void sendMail(EmailAddress email, URL url);
}
