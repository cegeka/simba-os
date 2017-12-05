package org.simbasecurity.core.service.communication;

import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.service.communication.mail.LinkGenerator;
import org.simbasecurity.core.service.communication.mail.MailService;
import org.simbasecurity.core.service.communication.token.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;

@Transactional
@Service
public class ResetPasswordService {

    @Autowired
    private MailService mailService;
    @Autowired
    private TokenGenerator tokenGenerator;
    @Autowired
    private LinkGenerator linkGenerator;

    public void sendMessage(User user) {
        Token token = tokenGenerator.generateToken(user);
        URL link = linkGenerator.generateResetPasswordLink(token);
        mailService.sendMail(user.getEmail(), link);
    }

}
