package org.simbasecurity.core.service.errors;

import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.api.service.thrift.TSimbaError;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SimbaExceptionHandlingCallerTest {

    private SimbaExceptionHandlingCaller caller;

    private String sideEffectedString = "unaffected";

    @Before
    public void setUp() throws Exception {
        caller = new SimbaExceptionHandlingCaller(new TSimbaErrorMappingHandler());
    }

    @Test
    public void callWithVoid_MappableSimbaMessageKey_ThrowsTSimbaError() throws Exception {
        assertThatThrownBy(() -> caller.call(this::voidMappableExceptionThrower))
            .isInstanceOf(TSimbaError.class);
    }

    @Test
    public void callWithVoid_UnmappableSimbaMessageKey_RethrowsSimbaException() throws Exception {
        assertThatThrownBy(() -> caller.call(this::voidUnmappableExceptionThrower))
            .isInstanceOf(SimbaException.class);
    }

    @Test
    public void callWithVoid_NoException_DoesNotThrowAnything() throws Exception {
        caller.call(this::voidMethod);
        assertThat(sideEffectedString).isEqualTo("sideEffected");
    }

    @Test
    public void callWithReturn_MappableSimbaMessageKey_ThrowsTSimbaError() throws Exception {
        assertThatThrownBy(() -> caller.call(this::returnTypeMappableExceptionThrower))
            .isInstanceOf(TSimbaError.class);
    }

    @Test
    public void callWithReturn_UnmappableSimbaMessageKey_RethrowsSimbaException() throws Exception {
        assertThatThrownBy(() -> caller.call(this::returnTypeUnmappableExceptionThrower))
            .isInstanceOf(SimbaException.class);
    }

    @Test
    public void callWithReturn_NoException_DoesNotThrowAnything() throws Exception {
        String actual = caller.call(this::returnTypeMethod);
        assertThat(actual).isEqualTo("newString");
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

    private String returnTypeUnmappableExceptionThrower() {
        throw new SimbaException(SimbaMessageKey.LANGUAGE_EMPTY);
    }

    private String returnTypeMethod() {
        return "newString";
    }
}