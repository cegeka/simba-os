package org.simbasecurity.core.service.communication.mail;

import java.util.logging.Logger;

public class MailServiceStub implements MailService {

    private static final Logger logger = Logger.getLogger(MailServiceStub.class.getName());

    @Override
    public void sendMail(Mail mail) {
        logger.info(() -> String.format("Email with link %s has been sent to %s", mail.getBody(), mail.getTo()));
    }
}
