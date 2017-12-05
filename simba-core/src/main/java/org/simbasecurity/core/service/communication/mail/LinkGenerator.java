package org.simbasecurity.core.service.communication.mail;

import org.simbasecurity.core.domain.communication.token.Token;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.UriBuilder;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.simbasecurity.common.config.SystemConfiguration.getSimbaWebURL;

@Service
public class LinkGenerator {

    public static final String SIMBA_NEW_PWD_PATH = "simba-new-pwd";

    public URL generateResetPasswordLink(Token token) {
        URI uri = buildResetPasswordURI(token);
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("%s is not a correctly formed URL.", uri));
        }
    }

    private URI buildResetPasswordURI(Token token) {
            return UriBuilder.fromPath(getSimbaWebURL())
                    .path(SIMBA_NEW_PWD_PATH)
                    .queryParam("token", token.asString())
                    .build();
    }

}
