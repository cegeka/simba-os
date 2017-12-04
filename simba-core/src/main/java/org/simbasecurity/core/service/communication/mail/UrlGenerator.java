package org.simbasecurity.core.service.communication.mail;

import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class UrlGenerator {
    public URL generateResetPasswordUrl(User user, Token token) {
        return null;
    }
}
