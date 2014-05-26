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

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.common.filter.action.AbstractAction;
import org.simbasecurity.common.filter.action.AddParameterToTargetAction;
import org.simbasecurity.common.filter.action.DeleteCookieAction;
import org.simbasecurity.common.filter.action.MakeCookieAction;
import org.simbasecurity.common.filter.action.RedirectAction;

public class FilterActionFactoryTest {

    private HttpServletRequest servletRequest = mock(HttpServletRequest.class);
    private HttpServletResponse servletResponse = mock(HttpServletResponse.class);
    private FilterChain filterChain = mock(FilterChain.class);

	@Test
	@SuppressWarnings("unchecked")
    public void testFillActionMap() {
    	
        Map<ActionType, Class<? extends AbstractAction>> map = new HashMap<ActionType, Class<? extends AbstractAction>>();
        new FilterActionFactory(servletRequest, servletResponse, filterChain).fillActionMap(map);
        
        Assert.assertTrue(map.values().containsAll(
                Arrays.asList(DoFilterAndSetPrincipalAction.class,
                              DoFilterAndSetPrincipalAction.class,
                              MakeCookieAction.class,
                              DeleteCookieAction.class,
                              RedirectAction.class,
                              AddParameterToTargetAction.class
                )));
    }

    @Test
    public void testCreateInstance() {
    	AbstractAction action = new FilterActionFactory(servletRequest, servletResponse, filterChain)
    		.createInstance(DoFilterAndSetPrincipalAction.class, new ActionDescriptor());
    	
    	Assert.assertTrue(action instanceof DoFilterAndSetPrincipalAction);
    }

}