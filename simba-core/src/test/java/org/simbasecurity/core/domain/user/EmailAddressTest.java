package org.simbasecurity.core.domain.user;

import org.junit.Test;
import org.simbasecurity.core.exception.SimbaException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EmailAddressTest {


    @Test
    public void creatingAnEmailAddressWithAnEmptyString_ThrowsException() throws Exception {
        assertThatThrownBy(() -> EmailAddress.email(null))
                .isInstanceOf(SimbaException.class)
                .hasMessage("EMAIL_ADDRESS_REQUIRED");
    }

    @Test
    public void creatingAnEmailAddressWithANullString_ThrowsException() throws Exception {
        assertThatThrownBy(() -> EmailAddress.email(""))
                .isInstanceOf(SimbaException.class)
                .hasMessage("EMAIL_ADDRESS_REQUIRED");
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
}