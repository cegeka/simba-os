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

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.session.CheckSessionCommand;
import org.simbasecurity.core.saml.SAMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EIDCheckSessionCommand extends CheckSessionCommand {

    @Autowired private SAMLService samlService;

    @Override
    protected void redirectToLogin(ChainContext context) {
        Map<String, String> parameters = new HashMap<>();
        String authRequestId = context.createLoginMapping().getToken();
        context.redirectWithParameters(getSAMLAuthRequest(authRequestId), parameters);
    }

    private String getSAMLAuthRequest(String authRequestId) {
        try {
            return samlService.getAuthRequestUrl(authRequestId, new Date());
        } catch (XMLStreamException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

