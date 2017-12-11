package org.simbasecurity.core.service.communication.reset.password;

import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.service.communication.mail.template.TemplateService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.when;
import static org.simbasecurity.core.domain.Language.nl_NL;
import static org.simbasecurity.core.service.communication.reset.password.ResetPasswordReason.FORGOT_PASSWORD;
import static org.simbasecurity.core.service.communication.reset.password.ResetPasswordReason.NEW_USER;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordTemplateServiceTest {

    @Mock
    private TemplateService templateServiceMock;
    @InjectMocks
    private ResetPasswordTemplateService templateFactory;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(templateFactory, "forgotPasswordMailTemplate", "forgotPasswordTemplate.vm");
        ReflectionTestUtils.setField(templateFactory, "newUserMailTemplate", "newUserTemplate.vm");
    }

    @Test
    public void newUser() throws Exception {
        when(templateServiceMock.createMailBody("newUserTemplate.vm", nl_NL, ImmutableMap.of("link", "link"))).thenReturn("someBody");
        String mailTemplate = templateFactory.createMailBody(NEW_USER, nl_NL, "link");

        Assertions.assertThat(mailTemplate).isEqualTo("someBody");
    }

    @Test
    public void forgotPassword() throws Exception {
        when(templateServiceMock.createMailBody("forgotPasswordTemplate.vm", nl_NL, ImmutableMap.of("link", "link"))).thenReturn("someBody");

        String mailTemplate = templateFactory.createMailBody(FORGOT_PASSWORD, nl_NL, "link");

        Assertions.assertThat(mailTemplate).isEqualTo("someBody");
    }
}