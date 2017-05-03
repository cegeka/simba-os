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
