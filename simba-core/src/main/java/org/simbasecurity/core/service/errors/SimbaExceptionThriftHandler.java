package org.simbasecurity.core.service.errors;

import org.simbasecurity.api.service.thrift.TSimbaError;
import org.simbasecurity.core.exception.SimbaException;

public interface SimbaExceptionThriftHandler {
    TSimbaError handle(SimbaException simbaException) throws TSimbaError;
}
