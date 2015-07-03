package org.simbasecurity.core.chain.eid;

import org.simbasecurity.common.request.RequestConstants;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.saml.SAMLResponseHandler;
import org.simbasecurity.core.saml.SAMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles a SAML Logout Response received from the IDP.
 */
@Component
public class SAMLLogoutCommand implements Command {

    @Autowired private SAMLService samlService;

    @Override
    public State execute(ChainContext context) throws Exception {
        String samlResponse = context.getRequestParameter(RequestConstants.SAML_RESPONSE);
        SAMLResponseHandler samlResponseHandler =
                samlService.getSAMLResponseHandler(samlResponse, context.getRequestURL());

        if (samlResponseHandler.isLogoutResponse()) {
            context.redirectToLogout();
            return State.FINISH;
        }

        return State.CONTINUE;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }
}
