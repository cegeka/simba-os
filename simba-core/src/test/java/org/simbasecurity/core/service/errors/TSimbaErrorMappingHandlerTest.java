package org.simbasecurity.core.service.errors;

import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.api.service.thrift.TSimbaError;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.simbasecurity.core.exception.SimbaMessageKey.*;

public class TSimbaErrorMappingHandlerTest {

    private TSimbaErrorMappingHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new TSimbaErrorMappingHandler();
    }

    @Test
    public void handle_canConvertAllSimbaMessageKeys() {
        List<SimbaMessageKey> ignoredSimbaMessageKeys = asList(EMPTY_USERNAME, EMPTY_PASSWORD, WRONG_PASSWORD, EMPTY_TARGET_URL, LOGIN_FAILED, ACCESS_DENIED, EMPTY_SUCCESS_URL, ACCOUNT_BLOCKED, MAIL_ERROR);

        Arrays.stream(values())
              .filter(messageKey -> !ignoredSimbaMessageKeys.contains(messageKey))
              .forEach(messageKey -> {
                  assertThatThrownBy(() ->
                          handler.handle(new SimbaException(messageKey, messageKey.name())))
                          .isInstanceOf(TSimbaError.class);
              });
    }
}