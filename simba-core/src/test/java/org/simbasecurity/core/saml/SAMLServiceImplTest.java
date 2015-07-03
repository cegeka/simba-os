package org.simbasecurity.core.saml;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationService;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.simbasecurity.core.saml.SAMLService.SAML_DATE_FORMAT;

@RunWith(MockitoJUnitRunner.class)
public class SAMLServiceImplTest {

    @InjectMocks private SAMLServiceImpl samlService;

    @Mock private ConfigurationService configurationService;

    @Before
    public void setup() {
        when(configurationService.<String>getValue(ConfigurationParameter.SAML_IDP_TARGET_URL)).thenReturn("https://idp.iamfas.belgium.be/fas");
    }

    @Test
    public void testCreateLogoutRequest() throws Exception {
        // String issueInstant = SAML_DATE_FORMAT.format(issueInstant);
        Date issueInstant = new Date();
        String logoutRequestId = "123456";
        System.out.println(samlService.createLogoutRequest(logoutRequestId, issueInstant));
    }
}