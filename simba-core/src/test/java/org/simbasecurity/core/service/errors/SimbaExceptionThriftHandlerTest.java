package org.simbasecurity.core.service.errors;

import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.api.service.thrift.TSimbaError;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SimbaExceptionThriftHandlerTest {

    private SimbaExceptionThriftHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new SimbaExceptionThriftHandler();
    }

    @Test
    public void handle_UnknownSimbaMessageKey_ReturnsOriginalSimbaException() {
        SimbaException simbaExceptionWithUnknownMessageKey = new SimbaException(SimbaMessageKey.LANGUAGE_EMPTY);

        assertThatThrownBy(() -> handler.handle(simbaExceptionWithUnknownMessageKey)).isEqualTo(simbaExceptionWithUnknownMessageKey);
    }

    @Test
    public void handle_KnownSimbaMessageKey_ReturnsTSimbaErrorWithConvertedMessageKey() {
        SimbaException emailAddressRequired = new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_REQUIRED, "User can't be created without an email...");

        assertThatThrownBy(() -> handler.handle(emailAddressRequired))
                .isEqualTo(new TSimbaError("error.email.is.required", "User can't be created without an email..."));
    }
}