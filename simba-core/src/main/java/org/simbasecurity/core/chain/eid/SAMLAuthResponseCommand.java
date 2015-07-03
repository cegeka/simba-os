package org.simbasecurity.core.chain.eid;

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.saml.SAMLResponseHandler;
import org.simbasecurity.core.saml.SAMLService;
import org.simbasecurity.core.service.LoginMappingService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.simbasecurity.common.request.RequestConstants.SAML_RESPONSE;

public class SAMLAuthResponseCommand implements Command {

    @Autowired
    private SAMLService samlService;

    @Autowired
    private LoginMappingService loginMappingService;

    @Override
    public State execute(ChainContext context) throws Exception {
        SAMLResponseHandler samlResponse = getSamlResponse(context);
        String loginToken = samlResponse.getInResponseTo();
        LoginMapping loginMapping = loginMappingService.getMapping(loginToken);
        if (loginMapping != null && !loginMapping.isExpired()) {
            context.setSAMLUser(
                new SAMLUser(
                    samlResponse.getAttribute("egovNRN"),
                    samlResponse.getAttribute("givenName"),
                    samlResponse.getAttribute("surname"),
                    samlResponse.getAttribute("mail"),
                    samlResponse.getAttribute("PrefLanguage")
                )
            );
            context.setLoginMapping(loginMapping);
            return State.CONTINUE;
        }

        context.redirectToAccessDenied();
        return State.FINISH;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

    private SAMLResponseHandler getSamlResponse(ChainContext context) throws Exception {
        String samlResponse = context.getRequestParameter(SAML_RESPONSE);
        return samlService.getSAMLResponseHandler(samlResponse, context.getRequestURL());
    }
}
