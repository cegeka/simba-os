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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;

public class AddParameterToTargetActionTest {

    @Test
    public void testExecute() throws Exception {
        ActionDescriptor actionDescriptor = new ActionDescriptor(new HashSet<ActionType>(), new HashMap<String, String>(), null, null, null);
        actionDescriptor.getActionTypes().add(ActionType.ADD_PARAMETER_TO_TARGET);

        String redirectURL = "http://localhost/redirect";
        actionDescriptor.setRedirectURL(redirectURL);
        actionDescriptor.getParameterMap().put("param1", "firstParam");
        actionDescriptor.getParameterMap().put("param2", "secondParam");

        AddParameterToTargetAction action = new AddParameterToTargetAction(actionDescriptor);
        action.execute();

        String resultUrl = action.getActionDescriptor().getRedirectURL();
        String expectedRedirectURL = redirectURL + "?param1=firstParam&param2=secondParam";
        assertEquals(expectedRedirectURL, resultUrl);
    }

}
