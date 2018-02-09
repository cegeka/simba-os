package org.simbasecurity.manager.service.rest.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SimbaManagerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({SimbaManagerException.class})
    public ResponseEntity<SimbaManagerException.SimbaManagerErrorRepresentation> handleSimbaManagerException(SimbaManagerException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(ex.toRepresentation());
    }
}
