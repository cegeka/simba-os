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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.common.request.RequestConstants;
import org.simbasecurity.core.chain.ChainContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IsSAMLResponseDecisionTest {

    @InjectMocks
    private IsSAMLResponseDecision isSAMLResponseDecision;

    @Mock
    private ChainContext contextMock;

    @Test
    public void testApplies_containsSAMLResponseRequestParameter_returnsTrue() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(RequestConstants.SAML_RESPONSE, "Base64AndURLEncodedResponse");

        when(contextMock.getRequestParameters()).thenReturn(parameters);

        assertTrue(isSAMLResponseDecision.applies(contextMock));
    }

    @Test
    public void testApplies_doesNotContainSAMLResponseRequestParameter_returnsFalse() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();

        when(contextMock.getRequestParameters()).thenReturn(parameters);

        assertFalse(isSAMLResponseDecision.applies(contextMock));
    }

}