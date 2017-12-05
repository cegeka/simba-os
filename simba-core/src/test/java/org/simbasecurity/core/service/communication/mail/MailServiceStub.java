package org.simbasecurity.core.service.communication.mail;

import org.simbasecurity.core.domain.user.EmailAddress;

import java.net.URL;
import java.util.logging.Logger;

public class MailServiceStub implements MailService {

    private static final Logger logger = Logger.getLogger(MailServiceStub.class.getName());

    @Override
    public void sendMail(EmailAddress email, URL url) {
        logger.info(() -> String.format("Email with link %s has been sent to %s", url.toString(), email.asString()));
    }
}
