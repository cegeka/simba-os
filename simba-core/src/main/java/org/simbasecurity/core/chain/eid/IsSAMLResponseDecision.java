package org.simbasecurity.core.chain.eid;

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Decision;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.simbasecurity.common.request.RequestConstants.SAML_RESPONSE;

@Component
public class IsSAMLResponseDecision implements Decision {

    @Override
    public boolean applies(ChainContext context) {
        return isSAMLResponseUsingRedirectBindingOrPostBinding(context.getRequestParameters());
    }

    private boolean isSAMLResponseUsingRedirectBindingOrPostBinding(Map<String, String> requestParameters) {
        return requestParameters.containsKey(SAML_RESPONSE);
    }
}
