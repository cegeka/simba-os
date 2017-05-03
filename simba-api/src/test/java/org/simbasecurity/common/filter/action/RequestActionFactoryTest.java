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
package org.simbasecurity.common.filter.action;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class RequestActionFactoryTest {

    private static final String REQUEST_VALUE = "value added to the request";
    private static final String RESPONSE_HEADER = "RESPONSE_HEADER";
    private static final String REQUEST_HEADER = "REQUEST_HEADER";

    private RequestActionFactory requestActionFactory;

    @Before
    public void setup() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(REQUEST_HEADER)).thenReturn(REQUEST_VALUE);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.containsHeader(RESPONSE_HEADER)).thenReturn(true);
        requestActionFactory = new RequestActionFactory(request, response);
    }

    @Test
    public void testCreate() {
        ActionDescriptor actionDescriptor = new ActionDescriptor(new HashSet<>(), new HashMap<>(), null, null, null, null);
        actionDescriptor.getActionTypes().add(ActionType.MAKE_COOKIE);
        actionDescriptor.getActionTypes().add(ActionType.REDIRECT);

        List<Action> result = requestActionFactory.create(actionDescriptor);
        Assert.assertEquals(2, result.size());
        for (Action aResult : result) {
            AbstractAction action = (AbstractAction) aResult;
            HttpServletRequest request = action.getHttpServletRequest();
            Assert.assertNotNull(request);
            Assert.assertEquals(REQUEST_VALUE, request.getHeader(REQUEST_HEADER));

            HttpServletResponse response = action.getHttpServletResponse();
            Assert.assertNotNull(response);
            Assert.assertTrue(response.containsHeader(RESPONSE_HEADER));
        }
    }

}
