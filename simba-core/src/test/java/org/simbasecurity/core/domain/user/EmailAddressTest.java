package org.simbasecurity.core.domain.user;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.LocatorRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class EmailAddressTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    @Rule public LocatorRule locatorRule = LocatorRule.locator();

    @Mock
    private CoreConfigurationService configurationServiceMock;

    @Before
    public void setUp() throws Exception {
        configurationServiceMock = locatorRule.getCoreConfigurationService();
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(true);
    }

    @Test
    public void creatingAnEmailAddressWithAnEmptyString_EmailRequiredParameterIsOn_ThrowsException() throws Exception {
        assertThatThrownBy(() -> EmailAddress.email(""))
                .isInstanceOf(SimbaException.class)
                .hasMessage("EMAIL_ADDRESS_REQUIRED");
    }

    @Test
    public void creatingAnEmailAddressWithANullString_EmailRequiredParameterIsOn_ThrowsException() throws Exception {
        assertThatThrownBy(() -> EmailAddress.email(null))
                .isInstanceOf(SimbaException.class)
                .hasMessage("EMAIL_ADDRESS_REQUIRED");
    }

    @Test
    public void creatingAnEmailAddressWithAnEmptyString_EmailRequiredParameterIsOff_DoesNotThrowException() throws Exception {
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);
        EmailAddress actual = EmailAddress.email("     ");
        assertThat(actual).isNotNull();
        assertThat(actual.asString()).isNull();
    }

    @Test
    public void creatingAnEmailAddressWithANullString_EmailRequiredParameterIsOff_DoesNotThrowException() throws Exception {
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);
        EmailAddress actual = EmailAddress.email(null);
        assertThat(actual).isNotNull();
        assertThat(actual.asString()).isNull();
    }

    @Test
    public void creatingAnEmailAddressWithAnInvalidEmailAddress_ThrowsException() throws Exception {
        assertThatThrownBy(() -> EmailAddress.email("bruce"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("bruce is not a valid email address");
        assertThatThrownBy(() -> EmailAddress.email("bruce@missingdomain"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("bruce@missingdomain is not a valid email address");
        assertThatThrownBy(() -> EmailAddress.email("t@test_wayneenterprises@wayneenterprises.com"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("t@test_wayneenterprises@wayneenterprises.com is not a valid email address");
        assertThatThrownBy(() -> EmailAddress.email("test@wayneenterprises"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("test@wayneenterprises is not a valid email address");
        assertThatThrownBy(() -> EmailAddress.email("@wayneenterprises.com"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("@wayneenterprises.com is not a valid email address");
        assertThatThrownBy(() -> EmailAddress.email("test@"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("test@ is not a valid email address");
        assertThatThrownBy(() -> EmailAddress.email("test@be."))
                .isInstanceOf(SimbaException.class)
                .hasMessage("test@be. is not a valid email address");
    }

    @Test
    public void creatingAnEmailAddressWithAValidEmailAddress_DoesNotThrowException() throws Exception {
        assertThat(EmailAddress.email("bruce@wayneenterprises.com").asString()).isEqualTo("bruce@wayneenterprises.com");
    }

    @Test
    public void nullSafeAsString_WhenEmailAddressIsNull_ReturnsNull() {
        assertThat(EmailAddress.nullSafeAsString(null)).isNull();
    }

    @Test
    public void nullSafeAsString_WhenEmailAddressNotNull_ReturnsAsString() {
        assertThat(EmailAddress.nullSafeAsString(EmailAddress.email("bruce@wayneenterprises.com"))).isEqualTo("bruce@wayneenterprises.com");
    }

    @Test
    public void equals_OnEmptyEmailAddress() {
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);
        EmailAddress nullEmail = EmailAddress.email(null);
        EmailAddress emptyEmail = EmailAddress.email("");
        EmailAddress blankEmail = EmailAddress.email("     ");

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