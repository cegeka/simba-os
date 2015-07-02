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