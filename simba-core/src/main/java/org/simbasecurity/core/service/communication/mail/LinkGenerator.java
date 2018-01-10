package org.simbasecurity.core.service.communication.mail;

import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.service.http.NewPasswordController;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.UriBuilder;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.simbasecurity.common.config.SystemConfiguration.getSimbaWebURL;

@Service
public class LinkGenerator {

    public URL generateResetPasswordLink(EmailAddress email, Token token) {
        URI uri = buildResetPasswordURI(email, token);
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("%s is not a correctly formed URL because %s", uri, e.getCause()));
        }
    }

    private URI buildResetPasswordURI(EmailAddress email, Token token) {
            return UriBuilder.fromPath(getSimbaWebURL())
                    .path("http")
                    .path(NewPasswordController.SIMBA_NEW_PWD_PATH)
                    .queryParam("email", email.asString())
                    .queryParam("token", token.asString())
                    .build();
    }

}
