package org.simbasecurity.core.service.errors;

import org.simbasecurity.api.service.thrift.TSimbaError;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.springframework.stereotype.Component;

import static org.simbasecurity.core.exception.SimbaMessageKey.*;

@Component
public class TSimbaErrorMappingHandler implements SimbaExceptionThriftHandler {

    @Override
    public TSimbaError handle(SimbaException simbaException) throws TSimbaError {
        mapSimbaMessageKeyToTSimbaError(PASSWORD_INVALID_LENGTH, "error.name.too.long", simbaException);
        mapSimbaMessageKeyToTSimbaError(PASSWORD_INVALID_COMPLEXITY, "error.password.invalid.complexity", simbaException);
        mapSimbaMessageKeyToTSimbaError(PASSWORDS_DONT_MATCH, "error.password.dont.match", simbaException);
        mapSimbaMessageKeyToTSimbaError(PASSWORD_SAME_AS_OLD, "error.password.same.as.old", simbaException);
        mapSimbaMessageKeyToTSimbaError(OPTIMISTIC_LOCK, "error.optimistic.lock", simbaException);
        mapSimbaMessageKeyToTSimbaError(INVALID_START_CONDITION, "error.invalid.start.condition", simbaException);
        mapSimbaMessageKeyToTSimbaError(INVALID_END_CONDITION, "error.invalid.end.condition", simbaException);
        mapSimbaMessageKeyToTSimbaError(USERNAME_EMPTY, "error.username.empty", simbaException);
        mapSimbaMessageKeyToTSimbaError(USERNAME_TOO_SHORT, "error.username.too.short", simbaException);
        mapSimbaMessageKeyToTSimbaError(USERNAME_TOO_LONG, "error.username.too.long", simbaException);
        mapSimbaMessageKeyToTSimbaError(USERNAME_INVALID, "error.username.invalid", simbaException);
        mapSimbaMessageKeyToTSimbaError(FIRSTNAME_TOO_SHORT, "error.firstname.too.short", simbaException);
        mapSimbaMessageKeyToTSimbaError(FIRSTNAME_TOO_LONG, "error.firstname.too.long", simbaException);
        mapSimbaMessageKeyToTSimbaError(NAME_TOO_SHORT, "error.name.too.short", simbaException);
        mapSimbaMessageKeyToTSimbaError(NAME_TOO_LONG, "error.name.too.long", simbaException);
        mapSimbaMessageKeyToTSimbaError(SUCCESSURL_TOO_LONG, "error.successurl.too.long", simbaException);
        mapSimbaMessageKeyToTSimbaError(LANGUAGE_EMPTY, "error.language.empty", simbaException);
        mapSimbaMessageKeyToTSimbaError(STATUS_EMPTY, "error.status.empty", simbaException);
        mapSimbaMessageKeyToTSimbaError(USER_ALREADY_EXISTS, "error.user.already.exists", simbaException);
        mapSimbaMessageKeyToTSimbaError(USER_DOESNT_EXISTS, "error.user.doesnt.exists", simbaException);
        mapSimbaMessageKeyToTSimbaError(EMAIL_ADDRESS_REQUIRED, "error.email.is.required", simbaException);
        mapSimbaMessageKeyToTSimbaError(EMAIL_ADDRESS_INVALID, "error.email.invalid", simbaException);
        mapSimbaMessageKeyToTSimbaError(USER_ALREADY_EXISTS_WITH_EMAIL, "error.user.already.exists.with.email", simbaException);
        mapSimbaMessageKeyToTSimbaError(LOGIN_TIME_EXPIRED, "error.login.time.expired", simbaException);
        throw simbaException;


    }

    private void mapSimbaMessageKeyToTSimbaError(SimbaMessageKey simbaMessageKey, String errorKey, SimbaException simbaException) throws TSimbaError {
        if (simbaMessageKey.equals(simbaException.getMessageKey())) {
            throw new TSimbaError(errorKey, simbaException.getMessage());
        }
    }
}
