/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
        if (!loginMappingService.isExpired(loginToken)) {
            context.setSAMLUser(
                new SAMLUser(
                    samlResponse.getAttribute("egovNRN"),
                    samlResponse.getAttribute("givenName"),
                    samlResponse.getAttribute("surname"),
                    samlResponse.getAttribute("mail"),
                    samlResponse.getAttribute("PrefLanguage")
                )
            );
            LoginMapping loginMapping = loginMappingService.getMapping(loginToken);
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
