package org.simbasecurity.core.service.communication.mail;

import org.apache.http.client.utils.URIBuilder;
import org.simbasecurity.core.domain.communication.token.Token;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.simbasecurity.common.config.SystemConfiguration.getSimbaWebURL;

@Service
public class LinkGenerator {

    public static final String SIMBA_NEW_PWD = "simba-new-pwd";

    public URL generateResetPasswordLink(Token token) {
        URI uri = buildResetPasswordURI(token);
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("%s is not a correctly formed URL.", uri));
        }
    }

    private URI buildResetPasswordURI(Token token) {
        URL simbaBaseUrl = getSimbaBaseUrl();
        try {
            return new URIBuilder()
                    .setScheme(simbaBaseUrl.getProtocol())
                    .setHost(simbaBaseUrl.getHost())
                    .setPort(simbaBaseUrl.getPort())
                    .setPath(String.format("%s/%s", simbaBaseUrl.getPath(), SIMBA_NEW_PWD))
                    .addParameter("token", token.asString())
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("%s contains an invalid character", token.asString()));
        }
    }

    private URL getSimbaBaseUrl(){
        String simbaWebURL = getSimbaWebURL();
        try {
            return new URL(simbaWebURL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("%s is not a correctly formed simba url.", simbaWebURL));
        }
    }
}
