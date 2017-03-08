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
package org.simbasecurity.common.filter.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RedirectActionTest {

    private static final String REQUEST_VALUE = "value added to the request";
    private static final String RESPONSE_HEADER = "RESPONSE_HEADER";
    private static final String REQUEST_HEADER = "REQUEST_HEADER";

    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setup() {
        request = mock(HttpServletRequest.class);
        when(request.getHeader(REQUEST_HEADER)).thenReturn(REQUEST_VALUE);
        response = mock(HttpServletResponse.class);
        when(response.containsHeader(RESPONSE_HEADER)).thenReturn(true);
    }

    @Test
    public void testExecute() throws Exception {
        ActionDescriptor actionDescriptor = new ActionDescriptor(new HashSet<>(), new HashMap<>(), null, null, null, null);
        actionDescriptor.getActionTypes().add(ActionType.REDIRECT);
        String redirectURL = "redirectURL";
        actionDescriptor.setRedirectURL(redirectURL);

        RedirectAction action = new RedirectAction(actionDescriptor);
        action.setRequest(request);
        action.setResponse(response);
        action.execute();

        Mockito.verify(response).sendRedirect(redirectURL);
    }

}
