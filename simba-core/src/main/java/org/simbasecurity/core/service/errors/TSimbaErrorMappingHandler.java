package org.simbasecurity.core.service.errors;

import org.simbasecurity.api.service.thrift.TSimbaError;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.springframework.stereotype.Component;

@Component
public class TSimbaErrorMappingHandler implements SimbaExceptionThriftHandler {

    @Override
    public TSimbaError handle(SimbaException simbaException) throws TSimbaError {
        if (SimbaMessageKey.EMAIL_ADDRESS_REQUIRED.equals(simbaException.getMessageKey())){
            throw new TSimbaError("error.email.is.required", simbaException.getMessage());
        }
        throw simbaException;
    }
}
