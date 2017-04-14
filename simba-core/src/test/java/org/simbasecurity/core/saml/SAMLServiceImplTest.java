package org.simbasecurity.core.saml;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationService;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.saml.SAMLService.SAML_DATE_FORMAT;

public class SAMLServiceImplTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks private SAMLServiceImpl samlService;

    @Mock private ConfigurationService configurationService;

    private static final String ISSUER = "FedletTest1";
    private static final String ASSERTION_CONSUMER_SERVICE_URL = "https://sp2.iamdemo.be:443/fedlet/fedletapplication";
    private static final String IDP_SLO_TARGET_URL = "https://idp.iamfas.int.belgium.be/fas/SPSloRedirect/metaAlias/sp";
    private static final Date ISSUE_INSTANT = new Date();
    private static final String REQUEST_ID = "rid123456";
    private static final String NAME_ID = "nid123456";
    private static final String SESSION_INDEX = "sid123456";
    private static final String LOGOUT_REQUEST = "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" " +
                                                    "ID=\"" + REQUEST_ID + "\" " +
                                                    "Version=\"2.0\" " +
                                                    "IssueInstant=\"" + SAML_DATE_FORMAT.format(ISSUE_INSTANT) + "\">" +
                                                    "<saml:Issuer xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
                                                        "https://iamapps.belgium.be/" +
                                                    "</saml:Issuer>" +
                                                    "<saml:NameID xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
                                                        "NameQualifier=\"" + IDP_SLO_TARGET_URL + "\" " +
                                                        "SPNameQualifier=\"https://iamapps.belgium.be/\" " +
                                                        "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\">" +
                                                        NAME_ID +
                                                    "</saml:NameID>" +
                                                    "<samlp:SessionIndex xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:protocol\">" +
                                                        SESSION_INDEX +
                                                    "</samlp:SessionIndex>" +
                                                "</samlp:LogoutRequest>";

    private static final String AUTH_REQUEST = "<samlp:AuthnRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" " +
                                                    "ID=\"" + REQUEST_ID + "\" " +
                                                    "Version=\"2.0\" " +
                                                    "IssueInstant=\"" + SAML_DATE_FORMAT.format(ISSUE_INSTANT) + "\" " +
                                                    "ForceAuthn=\"false\" " +
                                                    "IsPassive=\"false\" " +
                                                    "ProtocolBinding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" " +
                                                    "AssertionConsumerServiceURL=\"" + ASSERTION_CONSUMER_SERVICE_URL + "\">" +
                                                    "<saml:Issuer xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
                                                        ISSUER +
                                                    "</saml:Issuer>" +
                                                    "<samlp:NameIDPolicy " +
                                                        "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\" " +
                                                        "SPNameQualifier=\"" + ISSUER + "\" " +
                                                        "AllowCreate=\"true\">" +
                                                    "</samlp:NameIDPolicy>" +
                                                    "<samlp:RequestedAuthnContext xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" " +
                                                        "Comparison=\"exact\">" +
                                                        "<saml:AuthnContextClassRef xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
                                                            "urn:be:fedict:iam:fas:citizen:eid" +
                                                        "</saml:AuthnContextClassRef>" +
                                                    "</samlp:RequestedAuthnContext>" +
                                                "</samlp:AuthnRequest>";

    @Before
    public void setup() {
        when(configurationService.<String>getValue(SimbaConfigurationParameter.SAML_IDP_SLO_TARGET_URL)).thenReturn(IDP_SLO_TARGET_URL);
        when(configurationService.<String>getValue(SimbaConfigurationParameter.SAML_ASSERTION_CONSUMER_SERVICE_URL)).thenReturn(ASSERTION_CONSUMER_SERVICE_URL);
        when(configurationService.<String>getValue(SimbaConfigurationParameter.SAML_ISSUER)).thenReturn(ISSUER);
    }

    @Test
    public void testCreateLogoutRequest() throws Exception {
        assertEquals(samlService.encodeSAMLRequest(LOGOUT_REQUEST.getBytes()), samlService.createLogoutRequest(REQUEST_ID, ISSUE_INSTANT, NAME_ID, SESSION_INDEX));
    }

    @Test
    public void testCreateAuthRequest() throws Exception {
        assertEquals(samlService.encodeSAMLRequest(AUTH_REQUEST.getBytes()), samlService.createAuthRequest(REQUEST_ID, ISSUE_INSTANT));
    }
}