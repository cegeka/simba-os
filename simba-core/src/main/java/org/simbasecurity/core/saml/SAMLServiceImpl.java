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

package org.simbasecurity.core.saml;


import org.apache.commons.codec.binary.Base64;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import static org.simbasecurity.core.saml.SAMLConstants.*;

@Service("samlRequestService")
public class SAMLServiceImpl implements SAMLService {

    @Autowired private ConfigurationService configurationService;


    @Override
    public String createAuthRequest(String authRequestId, Date issueInstant) throws XMLStreamException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(baos);

        writer.writeStartElement("samlp", "AuthnRequest", NS_SAMLP);
        writer.writeNamespace("samlp", NS_SAMLP);

        writer.writeAttribute("ID", "_" + authRequestId);
        writer.writeAttribute("Version", "2.0");
        writer.writeAttribute("IssueInstant", SAML_DATE_FORMAT.format(issueInstant));
        writer.writeAttribute("ForceAuthn", "false");
        writer.writeAttribute("IsPassive", "false");
        writer.writeAttribute("ProtocolBinding", BINDING_HTTP_POST);
        writer.writeAttribute("AssertionConsumerServiceURL", configurationService.getValue(SimbaConfigurationParameter.SAML_ASSERTION_CONSUMER_SERVICE_URL));

        writer.writeStartElement("saml", "Issuer", NS_SAML);
        writer.writeNamespace("saml", NS_SAML);
        writer.writeCharacters(configurationService.getValue(SimbaConfigurationParameter.SAML_ISSUER));
        writer.writeEndElement();

        writer.writeStartElement("samlp", "NameIDPolicy", NS_SAMLP);

        writer.writeAttribute("Format", NAMEID_TRANSIENT);
        writer.writeAttribute("SPNameQualifier", configurationService.getValue(SimbaConfigurationParameter.SAML_ISSUER));
        writer.writeAttribute("AllowCreate", "true");
        writer.writeEndElement();

        writer.writeStartElement("samlp", "RequestedAuthnContext", NS_SAMLP);
        writer.writeNamespace("samlp", NS_SAMLP);
        writer.writeAttribute("Comparison", "exact");

        writer.writeStartElement("saml", "AuthnContextClassRef", NS_SAML);
        writer.writeNamespace("saml", NS_SAML);
        writer.writeCharacters(AC_FAS_EID);
        writer.writeEndElement();

        writer.writeEndElement();
        writer.writeEndElement();
        writer.flush();

        return encodeSAMLRequest(baos.toByteArray());
    }

    protected String encodeSAMLRequest(byte[] pSAMLRequest) throws RuntimeException {
        Base64 base64Encoder = new Base64();

        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);

            DeflaterOutputStream def = new DeflaterOutputStream(byteArray, deflater);
            def.write(pSAMLRequest);
            def.close();
            byteArray.close();

            String stream = new String(base64Encoder.encode(byteArray.toByteArray()));

            return stream.trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAuthRequestUrl(String authRequestId, Date issueInstant) throws XMLStreamException, IOException {
        String targetUrl = configurationService.getValue(SimbaConfigurationParameter.SAML_IDP_SSO_TARGET_URL);
        return generateSamlRedirectBindingUrl(createAuthRequest(authRequestId, issueInstant), targetUrl);
    }

    @Override
    public String createLogoutRequest(String logoutRequestId, Date issueInstant, String nameId, String sessionIndex) throws XMLStreamException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(baos);

        writer.writeStartElement("samlp", "LogoutRequest", NS_SAMLP);
        writer.writeNamespace("samlp", NS_SAMLP);

        writer.writeAttribute("ID", "_" + logoutRequestId);
        writer.writeAttribute("Version", "2.0");
        writer.writeAttribute("IssueInstant", SAML_DATE_FORMAT.format(issueInstant));

        writer.writeStartElement("saml", "Issuer", NS_SAML);
        writer.writeNamespace("saml", NS_SAML);
        writer.writeCharacters("https://iamapps.belgium.be/");
        writer.writeEndElement();

        writer.writeStartElement("saml", "NameID", NS_SAML);
        writer.writeNamespace("saml", NS_SAML);
        writer.writeAttribute("NameQualifier", configurationService.getValue(SimbaConfigurationParameter.SAML_IDP_SLO_TARGET_URL));
        writer.writeAttribute("SPNameQualifier", "https://iamapps.belgium.be/");
        writer.writeAttribute("Format", NAMEID_TRANSIENT);
        writer.writeCharacters(nameId);
        writer.writeEndElement();

        writer.writeStartElement("samlp", "SessionIndex", NS_SAMLP);
        writer.writeNamespace("saml", NS_SAMLP);
        writer.writeCharacters(sessionIndex);
        writer.writeEndElement();

        writer.writeEndElement();
        writer.flush();

        return encodeSAMLRequest(baos.toByteArray());
    }

    @Override
    public String getLogoutRequestUrl(String authRequestId, Date issueInstant, String nameId, String sessionIndex) throws XMLStreamException, IOException {
        String targetUrl = configurationService.getValue(SimbaConfigurationParameter.SAML_IDP_SLO_TARGET_URL);
        return generateSamlRedirectBindingUrl(createLogoutRequest(authRequestId, issueInstant, nameId, sessionIndex), targetUrl);
    }

    @Override
    public SAMLResponseHandler getSAMLResponseHandler(String response, String currentURL) throws Exception {
        return new SAMLResponseHandlerImpl(loadCertificate(), response, currentURL);
    }

    private String generateSamlRedirectBindingUrl(String authRequest, String idpTargetUrl) throws XMLStreamException, IOException {
        return idpTargetUrl + "?SAMLRequest=" + URLEncoder.encode(authRequest, "UTF-8");
    }

    private Certificate loadCertificate() throws CertificateException {
        String certificate = configurationService.getValue(SimbaConfigurationParameter.SAML_IDP_CERTIFICATE);
        CertificateFactory fty = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(certificate.getBytes()));
        return fty.generateCertificate(bais);
    }
}
