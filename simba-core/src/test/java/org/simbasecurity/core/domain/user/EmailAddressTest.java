package org.simbasecurity.core.domain.user;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.domain.StubEmailFactory;
import org.simbasecurity.core.service.config.CoreConfigurationService;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailAddressTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock
    private CoreConfigurationService configurationServiceMock;
    private StubEmailFactory emailFactory = StubEmailFactory.emailRequired();

    @Test
    public void nullSafeAsString_WhenEmailAddressIsNull_ReturnsNull() {
        assertThat(EmailAddress.nullSafeAsString(null)).isNull();
    }

    @Test
    public void nullSafeAsString_WhenEmailAddressNotNull_ReturnsAsString() {
        assertThat(EmailAddress.nullSafeAsString(emailFactory.email("bruce@wayneenterprises.com"))).isEqualTo("bruce@wayneenterprises.com");
    }

    @Test
    public void equals_OnEmptyEmailAddress() {
        emailFactory.setEmailRequired(false);

        EmailAddress nullEmail = emailFactory.email(null);
        EmailAddress emptyEmail = emailFactory.email("");
        EmailAddress blankEmail = emailFactory.email("     ");

        assertThat(nullEmail)
                .isEqualTo(nullEmail)
                .isEqualTo(emptyEmail)
                .isEqualTo(blankEmail);
        assertThat(emptyEmail)
                .isEqualTo(nullEmail)
                .isEqualTo(emptyEmail)
                .isEqualTo(blankEmail);
        assertThat(blankEmail)
                .isEqualTo(nullEmail)
                .isEqualTo(emptyEmail)
                .isEqualTo(blankEmail);
    }
}