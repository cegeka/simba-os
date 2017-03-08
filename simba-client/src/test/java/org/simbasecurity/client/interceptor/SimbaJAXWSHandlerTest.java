/*
 * Copyright 2013 Simba Open Source
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
 */
package org.simbasecurity.client.interceptor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.client.principal.SimbaPrincipal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.simbasecurity.common.config.SystemConfiguration.SYS_PROP_SIMBA_INTERNAL_SERVICE_URL;

public class SimbaJAXWSHandlerTest {

    private static final String SERVICE_URL_VALUE = "http://localhost:8880/simba/";
    private static final String REQUEST_URL = "http://localhost:8880/simba-petstore/HelloJAXWS";

    private SimbaJAXWSHandler handler;

    @Mock
    SOAPMessageContext messageContextMock;
    @Mock
    HttpServletRequest servletRequestMock;
    @Mock
    ServletContext servletContextMock;
    @Mock
    SOAPMessage messageMock;
    @Mock
    AuthenticationFilterService simbaServiceMock;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        System.setProperty(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, SERVICE_URL_VALUE);

        when(servletContextMock.getInitParameterNames()).thenReturn(Collections.enumeration(Collections.emptyList()));

        when(servletRequestMock.getParameterNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(servletRequestMock.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(servletRequestMock.getRequestURL()).thenReturn(new StringBuffer(REQUEST_URL));

        when(messageContextMock.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(Boolean.FALSE);
        when(messageContextMock.get(MessageContext.SERVLET_REQUEST)).thenReturn(servletRequestMock);
        when(messageContextMock.get(MessageContext.SERVLET_CONTEXT)).thenReturn(servletContextMock);

        when(messageContextMock.getMessage()).thenReturn(messageMock);

        handler = new SimbaJAXWSHandler();

//        injectSimbaServiceMock(handler);
    }

    private void injectSimbaServiceMock(SimbaJAXWSHandler handler) throws Exception {
        Field simbaServiceField = SimbaJAXWSHandler.class.getDeclaredField("simbaService");
        simbaServiceField.setAccessible(true);
        simbaServiceField.set(handler, simbaServiceMock);
    }

    @Test
    public void noAuthenticationOnOutboundMessages() {
        when(messageContextMock.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).thenReturn(Boolean.TRUE);

        handler.handleMessage(messageContextMock);

        verify(messageContextMock, never()).getMessage();
    }

    @Test
    @Ignore
    // TODO: Create integration test
    public void soapAuthenticationSuccessful() throws Exception {
        ActionDescriptor actionDescriptor = new ActionDescriptor(new HashSet<ActionType>(), new HashMap<String, String>(), null, null, null, null);
        actionDescriptor.getActionTypes().add(ActionType.DO_FILTER_AND_SET_PRINCIPAL);
        actionDescriptor.setPrincipal("principal");

        // when(simbaServiceMock.processRequest(getChainConfiguration())).thenReturn(actionDescriptor);
        handler.handleMessage(messageContextMock);

        verify(messageContextMock).put(SimbaPrincipal.SIMBA_USER_CTX_KEY, "principal");
    }


    @Test(expected = SimbaWSAuthenticationException.class)
    @Ignore
    // TODO: Create integration test
    public void soapAuthenticationThrowsExceptionWhenUnsuccessful() throws Exception {
        ActionDescriptor actionDescriptor = new ActionDescriptor();

        // when(simbaServiceMock.processRequest(getChainConfiguration())).thenReturn(actionDescriptor);

        handler.handleMessage(messageContextMock);
    }
}
