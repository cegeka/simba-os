package org.simbasecurity.core.service.errors;

import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.simbasecurity.api.service.thrift.TSimbaError;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.org.lidalia.slf4jext.Level.ERROR;

public class SimbaExceptionHandlingCallerTest {

    @Rule
    public TestLoggerFactoryResetRule loggerRule = new TestLoggerFactoryResetRule();

    private SimbaExceptionHandlingCaller caller;

    private String sideEffectedString = "unaffected";

    @Before
    public void setUp() throws Exception {
        caller = new SimbaExceptionHandlingCaller(new TSimbaErrorMappingHandler());
    }

    @Test
    public void callWithVoid_MappableSimbaMessageKey_ThrowsTSimbaError_AndLogs() throws Exception {
        assertThatThrownBy(() -> caller.call(this::voidMappableExceptionThrower))
            .isInstanceOf(TSimbaError.class);

        assertThat(TestLoggerFactory.getLoggingEvents())
                .extracting(LoggingEvent::getLevel, LoggingEvent::getMessage)
                .contains(Tuple.tuple(ERROR, "EMAIL_ADDRESS_REQUIRED"));
    }

    @Test
    public void callWithVoid_NoException_DoesNotThrowAnything_AndLogsNothing() throws Exception {
        caller.call(this::voidMethod);

        assertThat(sideEffectedString).isEqualTo("sideEffected");
        assertThat(TestLoggerFactory.getLoggingEvents()).isEmpty();
    }

    @Test
    public void callWithReturn_MappableSimbaMessageKey_ThrowsTSimbaError_AndLogs() throws Exception {
        assertThatThrownBy(() -> caller.call(this::returnTypeMappableExceptionThrower))
            .isInstanceOf(TSimbaError.class);

        assertThat(TestLoggerFactory.getLoggingEvents())
                .extracting(LoggingEvent::getLevel, LoggingEvent::getMessage)
                .contains(Tuple.tuple(ERROR, "EMAIL_ADDRESS_REQUIRED"));
    }

    @Test
    public void callWithReturn_NoException_DoesNotThrowAnything_AndLogsNothing() throws Exception {
        String actual = caller.call(this::returnTypeMethod);

        assertThat(actual).isEqualTo("newString");
        assertThat(TestLoggerFactory.getLoggingEvents()).isEmpty();
    }

    private void voidMappableExceptionThrower() {
        throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_REQUIRED);
    }

    private void voidUnmappableExceptionThrower() {
        throw new SimbaException(SimbaMessageKey.LANGUAGE_EMPTY);
    }

    private void voidMethod() {
        sideEffectedString = "sideEffected";
    }

    private String returnTypeMappableExceptionThrower() {
        throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_REQUIRED);
    }

    private String returnTypeMethod() {
        return "newString";
    }
}