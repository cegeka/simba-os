package org.simbasecurity.core.chain.eid;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.saml.SAMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.simbasecurity.common.request.RequestConstants.SAML_RESPONSE;
import static org.simbasecurity.core.audit.AuditMessages.INVALID_SAML_RESPONSE;
import static org.simbasecurity.core.audit.AuditMessages.VALID_SAML_RESPONSE;

/**
 * The ValidateSAMLParametersCommand checks if the request contains...
 *
 * @since 2.1.3
 */
@Component
public class ValidateSAMLParametersCommand implements Command {

    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

    @Autowired private SAMLService samlService;

    @Override
    public State execute(ChainContext context) throws Exception {
        String samlResponse = context.getRequestParameter(SAML_RESPONSE);

        if (!samlService.getSAMLResponseHandler(samlResponse, context.getRequestURL()).isValid()) {
            context.redirectToAccessDenied(); // Check
            logFailure(context, INVALID_SAML_RESPONSE);
            return State.FINISH;
        }

        logSuccess(context, VALID_SAML_RESPONSE);
        return State.CONTINUE;
    }

    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

    @Override
    public void logSuccess(ChainContext context, String message) {
        audit.log(auditLogFactory.createEventForAuthenticationForSuccess(context, message));
    }

    @Override
    public void logFailure(ChainContext context, String message) {
        audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, message));
    }
}
