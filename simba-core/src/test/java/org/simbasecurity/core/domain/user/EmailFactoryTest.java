package org.simbasecurity.core.domain.user;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.CoreConfigurationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class EmailFactoryTest {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private CoreConfigurationService configurationServiceMock;

    @InjectMocks private EmailFactory emailFactory;

    @Before
    public void setUp() throws Exception {
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(true);
    }

    @Test
    public void creatingAnEmailAddressWithAnEmptyString_EmailRequiredParameterIsOn_ThrowsException() throws Exception {
        assertThatThrownBy(() -> emailFactory.email(""))
                .isInstanceOf(SimbaException.class)
                .hasMessage("EMAIL_ADDRESS_REQUIRED");
    }

    @Test
    public void creatingAnEmailAddressWithANullString_EmailRequiredParameterIsOn_ThrowsException() throws Exception {
        assertThatThrownBy(() -> emailFactory.email(null))
                .isInstanceOf(SimbaException.class)
                .hasMessage("EMAIL_ADDRESS_REQUIRED");
    }

    @Test
    public void creatingAnEmailAddressWithAnEmptyString_EmailRequiredParameterIsOff_DoesNotThrowException() throws Exception {
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);
        EmailAddress actual = emailFactory.email("     ");
        assertThat(actual).isNotNull();
        assertThat(actual.asString()).isNull();
    }

    @Test
    public void creatingAnEmailAddressWithANullString_EmailRequiredParameterIsOff_DoesNotThrowException() throws Exception {
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED)).thenReturn(false);
        EmailAddress actual = emailFactory.email(null);
        assertThat(actual).isNotNull();
        assertThat(actual.asString()).isNull();
    }

    @Test
    public void creatingAnEmailAddressWithAnInvalidEmailAddress_ThrowsException() throws Exception {
        assertThatThrownBy(() -> emailFactory.email("bruce"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("bruce is not a valid email address");
        assertThatThrownBy(() -> emailFactory.email("bruce@missingdomain"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("bruce@missingdomain is not a valid email address");
        assertThatThrownBy(() -> emailFactory.email("t@test_wayneenterprises@wayneenterprises.com"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("t@test_wayneenterprises@wayneenterprises.com is not a valid email address");
        assertThatThrownBy(() -> emailFactory.email("test@wayneenterprises"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("test@wayneenterprises is not a valid email address");
        assertThatThrownBy(() -> emailFactory.email("@wayneenterprises.com"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("@wayneenterprises.com is not a valid email address");
        assertThatThrownBy(() -> emailFactory.email("test@"))
                .isInstanceOf(SimbaException.class)
                .hasMessage("test@ is not a valid email address");
        assertThatThrownBy(() -> emailFactory.email("test@be."))
                .isInstanceOf(SimbaException.class)
                .hasMessage("test@be. is not a valid email address");
    }

    @Test
    public void creatingAnEmailAddressWithAValidEmailAddress_DoesNotThrowException() throws Exception {
        assertThat(emailFactory.email("bruce@wayneenterprises.com").asString()).isEqualTo("bruce@wayneenterprises.com");
    }

}