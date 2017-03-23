package org.simbasecurity.core.chain.eid;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.saml.SAMLResponseHandler;
import org.simbasecurity.core.saml.SAMLService;
import org.simbasecurity.core.service.LoginMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.simbasecurity.common.request.RequestConstants.SAML_RESPONSE;

@Component
public class SAMLAuthResponseCommand implements Command {

    @Autowired private SAMLService samlService;
    @Autowired private LoginMappingService loginMappingService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

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
            auditLog(context, samlResponse);
            return State.CONTINUE;
        }

        context.redirectToAccessDenied();
        auditLog(context, samlResponse);
        return State.FINISH;
    }

    private void auditLog(ChainContext context, SAMLResponseHandler samlResponse) {
        String messageID = samlResponse.getMessageID();
        String timestamp = samlResponse.getIssueInstant();
        String endUser = samlResponse.getAttribute("uid");
        audit.log(auditLogFactory.createEventForAuthenticationEIDSAMLResponse(context, messageID, timestamp, endUser));
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
