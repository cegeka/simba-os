package org.simbasecurity.core.service.errors;

import org.simbasecurity.api.service.thrift.TSimbaError;
import org.simbasecurity.core.exception.SimbaException;

public class ForwardingThriftHandlerForTests implements SimbaExceptionThriftHandler {

    public static ForwardingThriftHandlerForTests forwardingThriftHandlerForTests(){
        return new ForwardingThriftHandlerForTests();
    }

    private ForwardingThriftHandlerForTests() {
    }

    @Override
    public TSimbaError handle(SimbaException simbaException) throws TSimbaError {
        throw simbaException;
    }
}