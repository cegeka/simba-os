package org.simbasecurity.core.service.communication.mail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.domain.StubEmailFactory;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.domain.user.EmailFactory;
import org.simbasecurity.core.service.config.CoreConfigurationService;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.PASSWORD_RESET_TOKEN_URL;
import static org.simbasecurity.core.domain.communication.token.Token.generateToken;

@RunWith(MockitoJUnitRunner.class)
public class LinkGeneratorTest {

    @Mock private CoreConfigurationService configurationServiceMock;

    private LinkGenerator linkGenerator;

    private EmailFactory emailFactory = StubEmailFactory.emailRequired();

    @Before
    public void setUp() throws Exception {
        linkGenerator = new LinkGenerator(configurationServiceMock);
    }

    @Test
    public void generateResetPasswordUrl_WillGenerateLinksToResetPassword_BasedOnSimbaProperty_WithTokenAndUsername() throws Exception {
        List<String> links = Arrays.asList("https://www.simba.be:1000/simba", "https://www.dag.no:8080/FYFAEN");
        when(configurationServiceMock.getValue(PASSWORD_RESET_TOKEN_URL)).thenReturn(links);
        EmailAddress email = emailFactory.email("myEmail@myProvider.com");
        String urlEscapedEmail = "myEmail%40myProvider.com";
        Token token = generateToken();

        List<URL> urls = linkGenerator.generateResetPasswordLinks(email, token);

        String queryParams = String.format("email=%s&token=%s", urlEscapedEmail, token.asString());
        assertThat(urls).extracting(
                URL::getHost,
                URL::getProtocol,
                URL::getPort,
                URL::getQuery,
                URL::getPath
        ).containsExactly(
                tuple("www.simba.be", "https", 1000, queryParams, "/simba/http/simba-new-pwd"),
                tuple("www.dag.no", "https", 8080, queryParams, "/FYFAEN/http/simba-new-pwd")
        );
    }
}