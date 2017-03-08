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
package org.simbasecurity.client.filter.action;

import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.client.filter.request.HttpServletRequestWithPrincipal;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DoFilterAndSetPrincipalActionTest {

    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testExecute_withPrincipal() throws Exception {
        FilterChain filterChain = mock(FilterChain.class);

        ActionDescriptor actionDescriptor = new ActionDescriptor(new HashSet<ActionType>(), new HashMap<String, String>(), null, null, null,null );
        actionDescriptor.getActionTypes().add(ActionType.DO_FILTER_AND_SET_PRINCIPAL);
        actionDescriptor.setPrincipal("principal");

        DoFilterAndSetPrincipalAction action = new DoFilterAndSetPrincipalAction(actionDescriptor);
        action.setRequest(request);
        action.setResponse(response);
        action.setFilterChain(filterChain);
        action.execute();
        verify(filterChain).doFilter(any(HttpServletRequestWithPrincipal.class), any(HttpServletResponse.class));
    }

    @Test
    public void testExecute_withoutPrincipal() throws Exception {
        FilterChain filterChain = mock(FilterChain.class);

        ActionDescriptor actionDescriptor = new ActionDescriptor(new HashSet<ActionType>(), new HashMap<String, String>(), null, null, null, null);
        actionDescriptor.getActionTypes().add(ActionType.DO_FILTER_AND_SET_PRINCIPAL);

        DoFilterAndSetPrincipalAction action = new DoFilterAndSetPrincipalAction(actionDescriptor);
        action.setRequest(request);
        action.setResponse(response);
        action.setFilterChain(filterChain);
        action.execute();
        verify(filterChain).doFilter(request, response);
    }

}
