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
package org.simbasecurity.client.interceptor;

import com.sun.security.auth.UserPrincipal;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.client.principal.SimbaPrincipal;
import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.request.RequestUtil;
import org.simbasecurity.common.util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.Principal;
import java.util.Collections;
import java.util.Set;

import static org.simbasecurity.common.xpath.WSSEConstants.SECURITY_Q_NAME;

public final class SimbaJAXWSHandler implements SOAPHandler<SOAPMessageContext> {

    private String simbaURL;
    private String simbaWebURL;

    @Override
    public void close(final MessageContext context) {
    }

    @Override
    public boolean handleFault(final SOAPMessageContext context) {
        return true;
    }

    @Override
    public boolean handleMessage(final SOAPMessageContext context) {
        if (isInboundMessage(context)) {
            try {
                final SOAPHeader header = context.getMessage().getSOAPHeader();

                final HttpServletRequest httpServletRequest = (HttpServletRequest) context.get(MessageContext.SERVLET_REQUEST);
                final ServletContext servletContext = (ServletContext) context.get(MessageContext.SERVLET_CONTEXT);

                final RequestData requestData = RequestUtil.createWSSERequestData(httpServletRequest, header,
                                                                                  getSimbaWebURL(servletContext));

                THttpClient tHttpClient = null;
                try {
                    tHttpClient = new THttpClient(SimbaConfiguration.getSimbaAuthenticationURL());
                    TProtocol tProtocol = new TJSONProtocol(tHttpClient);

                    AuthenticationFilterService.Client authenticationClient = new AuthenticationFilterService.Client(tProtocol);

                    ActionDescriptor actionDescriptor = authenticationClient.processRequest(requestData, "wsLoginChain");
                    if (!actionDescriptor.getActionTypes().contains(ActionType.DO_FILTER_AND_SET_PRINCIPAL)) {
                        throw new SimbaWSAuthenticationException("Authentication Failed");
                    }

                    String username = actionDescriptor.getPrincipal();

                    Principal principal = null;
                    if (username != null) {
                        principal = new UserPrincipal(username);
                    }
                    if (principal != null) {
                        context.put(SimbaPrincipal.SIMBA_USER_CTX_KEY, principal);
                        context.setScope(SimbaPrincipal.SIMBA_USER_CTX_KEY, MessageContext.Scope.APPLICATION);
                    }
                } finally {
                    if (tHttpClient != null) {
                        tHttpClient.close();
                    }
                }
            } catch (Exception e) {
                throw new SimbaWSAuthenticationException("Authentication Failed", e);
            }
        }

        return true;
    }

    private boolean isInboundMessage(final SOAPMessageContext context) {
        return !((Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY));
    }

    @Override
    public Set<QName> getHeaders() {
        return Collections.singleton(SECURITY_Q_NAME);
    }

    private String getSimbaURL(final ServletContext servletContext) {
        if (simbaURL == null) {
            simbaURL = SystemConfiguration.getSimbaServiceURL(servletContext) + "/authenticationService";
            checkUrlPresent(simbaURL);
        }
        return simbaURL;
    }

    private void checkUrlPresent(final String url) {
        if (StringUtil.isEmpty(url)) {
            throw new SimbaWSAuthenticationException(
                    "Simba URL has not been set. Check servlet context params or system property ["
                    + SystemConfiguration.SYS_PROP_SIMBA_INTERNAL_SERVICE_URL + "]");
        }
    }

    private String getSimbaWebURL(final ServletContext servletContext) {
        if (simbaWebURL == null) {
            simbaWebURL = SystemConfiguration.getSimbaWebURL(servletContext);
        }
        return simbaWebURL;
    }
}