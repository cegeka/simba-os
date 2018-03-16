package org.simbasecurity.core.service.communication.mail;

import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.core.service.http.NewPasswordController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.UriBuilder;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.PASSWORD_RESET_TOKEN_URL;

@Service
public class LinkGenerator {

    private CoreConfigurationService configurationService;

    @Autowired
    public LinkGenerator(CoreConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public List<URL> generateResetPasswordLinks(EmailAddress email, Token token) {
        List<String> urls = configurationService.getValue(PASSWORD_RESET_TOKEN_URL);
        return urls.stream()
                .map(baseUrl -> buildResetPasswordURI(baseUrl, email, token))
                .map(toURL())
                .collect(toList());
    }

    private URI buildResetPasswordURI(String baseURL, EmailAddress email, Token token) {
        return UriBuilder.fromPath(baseURL)
                .path("http")
                .path(NewPasswordController.SIMBA_NEW_PWD_PATH)
                .queryParam("email", email.asString())
                .queryParam("token", token.asString())
                .build();
    }

    private Function<URI, URL> toURL() {
        return uri -> {
            try {
                return uri.toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(String.format("%s is not a correctly formed URL because %s", uri, e.getCause()));
            }
        };
    }

}
