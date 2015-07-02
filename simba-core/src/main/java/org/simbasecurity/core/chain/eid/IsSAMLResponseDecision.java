package org.simbasecurity.core.chain.eid;

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Decision;

import java.util.Map;

import static org.simbasecurity.common.request.RequestConstants.SAML_RESPONSE;

public class IsSAMLResponseDecision implements Decision {

    @Override
    public boolean applies(ChainContext context) {
        return isSAMLResponseUsingRedirectBinding(context.getRequestParameters());
    }

    private boolean isSAMLResponseUsingRedirectBinding(Map<String, String> requestParameters) {
        return requestParameters.containsKey(SAML_RESPONSE);
    }
}
