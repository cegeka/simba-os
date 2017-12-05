package org.simbasecurity.core.service.communication.mail;

import org.junit.After;
import org.junit.Test;
import org.simbasecurity.core.domain.communication.token.Token;

import java.net.URL;

import static java.lang.System.getProperties;
import static java.lang.System.setProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.simbasecurity.common.config.SystemConfiguration.SYS_PROP_SIMBA_WEB_URL;
import static org.simbasecurity.common.config.SystemConfiguration.getSimbaWebURL;
import static org.simbasecurity.core.domain.communication.token.Token.generateToken;

public class LinkGeneratorTest {

    public static final String WEB_URL_SYSPROP_VALUE = "http://www.simba.be/simba";
    private LinkGenerator linkGenerator = new LinkGenerator();

    @After
    public void tearDown() throws Exception {
        getProperties().remove(SYS_PROP_SIMBA_WEB_URL);
    }

    @Test
    public void generateResetPasswordUrl_WillGenerateLinkToResetPassword_WithTokenAndUsername() throws Exception {
        setProperty(SYS_PROP_SIMBA_WEB_URL, WEB_URL_SYSPROP_VALUE);
        Token token = generateToken();

        URL url = linkGenerator.generateResetPasswordLink(token);

        URL simbaUrl = new URL(getSimbaWebURL());

        assertThat(url).isNotNull();
        assertThat(url.getHost()).isEqualTo(simbaUrl.getHost());
        assertThat(url.getProtocol()).isEqualTo(simbaUrl.getProtocol());
        assertThat(url.getPort()).isEqualTo(simbaUrl.getPort());
        assertThat(url.getQuery()).isEqualTo(String.format("token=%s", token.asString()));
        assertThat(url.getPath()).isEqualTo("/simba/simba-new-pwd");
    }
}