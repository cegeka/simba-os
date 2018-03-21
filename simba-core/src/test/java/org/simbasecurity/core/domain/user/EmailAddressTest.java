package org.simbasecurity.core.domain.user;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.LocatorRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class EmailAddressTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    @Rule public LocatorRule locatorRule = LocatorRule.locator();

    @Mock
    private CoreConfigurationService configurationServiceMock;
    private EmailFactory emailFactory;

    @Before
    public void setUp() throws Exception {

        configurationServiceMock = locatorRule.getCoreConfigurationService();
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(true);

        emailFactory = new EmailFactory(configurationServiceMock);
    }

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
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);
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