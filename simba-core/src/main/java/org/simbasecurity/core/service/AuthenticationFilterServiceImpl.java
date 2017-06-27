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
package org.simbasecurity.core.service;

import org.apache.thrift.TException;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.ChainContextImpl;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;
import static org.simbasecurity.core.locator.GlobalContext.locate;

@Service("authenticationFilterService")
public class AuthenticationFilterServiceImpl implements AuthenticationFilterService.Iface {

    @Autowired private SessionService sessionService;
    @Autowired private CoreConfigurationService configurationService;
    @Autowired private LoginMappingService loginMapping;
    @Autowired private SSOTokenMappingService ssoTokenMappingService;

    @Transactional
    public ActionDescriptor processRequest(RequestData requestData, String chainCommand) throws TException {
        if (requestData == null) {
            throw new IllegalArgumentException("Parameter 'requestData' can not be null");
        }
        if (chainCommand == null) {
            throw new IllegalArgumentException("Parameter 'chainCommand' can not be null");
        }
        Command chain = locateCommandChain(chainCommand);
        Session currentSession = getCurrentSession(requestData);
        ChainContext chainContext = createChainContext(requestData, currentSession);
        try {
            chain.execute(chainContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return chainContext.getActionDescriptor();
    }

    private Command locateCommandChain(String chainCommand) {
        Object command = locate(chainCommand);
        if (!(command instanceof Command)) {
            throw new IllegalArgumentException("The specified bean ('" + chainCommand + "') isn't a " + Command.class.getSimpleName());
        }
        return (Command) command;
    }

    private ChainContext createChainContext(RequestData request, Session currentSession) {
        return new ChainContextImpl(request, currentSession, configurationService,loginMapping);
    }

    Session getCurrentSession(RequestData requestData) {
        SSOToken ssoToken;
        if (requestData.isSsoTokenMappingKeyProvided()) {
            String ssoTokenKey = requestData.getRequestParameters().get(SIMBA_SSO_TOKEN);
            ssoToken = ssoTokenMappingService.getSSOToken(ssoTokenKey);
            if (ssoToken != null) {
                ssoTokenMappingService.destroyMapping(ssoTokenKey);
            }
        } else {
            ssoToken = requestData.getSsoToken();
        }
        return sessionService.getSession(ssoToken);
    }
}
