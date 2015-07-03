package org.simbasecurity.core.saml;


import org.apache.commons.codec.binary.Base64;
import org.simbasecurity.core.config.ConfigurationParameter;
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
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import static org.simbasecurity.core.saml.SAMLConstants.*;

@Service("samlRequestService")
public class SAMLServiceImpl implements SAMLService {

    @Autowired private ConfigurationService configurationService;


    @Override
    public String createAuthRequest(String authRequestId) throws XMLStreamException, IOException {
        final String issueInstant = SAML_DATE_FORMAT.format(new Date());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(baos);

        writer.writeStartElement("samlp", "AuthnRequest", NS_SAMLP);
        writer.writeNamespace("samlp", NS_SAMLP);

        writer.writeAttribute("ID", authRequestId);
        writer.writeAttribute("Version", "2.0");
        writer.writeAttribute("IssueInstant", issueInstant);
        writer.writeAttribute("ForceAuthn", "false");
        writer.writeAttribute("IsPassive", "false");
        writer.writeAttribute("ProtocolBinding", BINDING_HTTP_POST);
        writer.writeAttribute("AssertionConsumerServiceURL", configurationService.<String>getValue(ConfigurationParameter.SAML_ASSERTION_CONSUMER_SERVICE_URL));

        writer.writeStartElement("saml", "Issuer", NS_SAML);
        writer.writeNamespace("saml", NS_SAML);
        writer.writeCharacters(configurationService.<String>getValue(ConfigurationParameter.SAML_ISSUER));
        writer.writeEndElement();

        writer.writeStartElement("samlp", "NameIDPolicy", NS_SAMLP);

        writer.writeAttribute("Format", NAMEID_TRANSIENT);
        writer.writeAttribute("SPNameQualifier", configurationService.<String>getValue(ConfigurationParameter.SAML_ISSUER));
        writer.writeAttribute("AllowCreate", "true");
        writer.writeEndElement();

        writer.writeStartElement("samlp", "RequestedAuthnContext", NS_SAMLP);

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
    public String getAuthRequestUrl(String authRequestId) throws XMLStreamException, IOException {
        return generateSamlRedirectBindingUrl(createAuthRequest(authRequestId));
    }

    @Override
    public String createLogoutRequest(String logoutRequestId) throws XMLStreamException, IOException {
        final String issueInstant = SAML_DATE_FORMAT.format(new Date());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(baos);

        writer.writeStartElement("samlp", "LogoutRequest", NS_SAMLP);
        writer.writeNamespace("samlp", NS_SAMLP);

        writer.writeAttribute("ID", logoutRequestId);
        writer.writeAttribute("Version", "2.0");
        writer.writeAttribute("IssueInstant", issueInstant);

        writer.writeStartElement("saml", "Issuer", NS_SAML);
        writer.writeNamespace("saml", NS_SAML);
        writer.writeCharacters("https://iamapps.belgium.be/");
        writer.writeEndElement();

        writer.writeStartElement("saml", "NameID", NS_SAML);

        writer.writeAttribute("NameQualifier", configurationService.<String>getValue(ConfigurationParameter.SAML_IDP_TARGET_URL));
        writer.writeAttribute("SPNameQualifier", "https://iamapps.belgium.be/");
        writer.writeAttribute("Format", NAMEID_TRANSIENT);
        writer.writeCharacters(UUID.randomUUID().toString());
        writer.writeEndElement();

        writer.writeStartElement("samlp", "SessionIndex", NS_SAMLP);

        writer.writeCharacters(UUID.randomUUID().toString());
        writer.writeEndElement();

        writer.writeEndElement();
        writer.flush();

        return encodeSAMLRequest(baos.toByteArray());
    }

    @Override
    public String getLogoutRequestUrl(String authRequestId) throws XMLStreamException, IOException {
        return generateSamlRedirectBindingUrl(createLogoutRequest(authRequestId));
    }

    @Override
    public SAMLResponseHandler getSAMLResponseHandler(String response, String currentURL) throws Exception {
        return new SAMLResponseHandlerImpl(loadCertificate(), response, currentURL);
    }

    private String generateSamlRedirectBindingUrl(String authRequest) throws XMLStreamException, IOException {
        return configurationService.getValue(ConfigurationParameter.SAML_IDP_TARGET_URL) + "?SAMLRequest=" + URLEncoder.encode(authRequest, "UTF-8");
    }

    private Certificate loadCertificate() throws CertificateException {
        String certificate = configurationService.getValue(ConfigurationParameter.SAML_IDP_CERTIFICATE);
        CertificateFactory fty = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(certificate.getBytes()));
        return fty.generateCertificate(bais);
    }
}
