package org.simbasecurity.core.saml;

import org.apache.commons.codec.binary.Base64;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import javax.xml.crypto.MarshalException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Scanner;

import static org.junit.Assert.*;

public class UtilsTest {

    @Rule
    public ExpectedException assertException = ExpectedException.none();

    @Test
    public void testXMLAttacks_XXE() throws Exception {
        assertException.expect(SecurityException.class);
        assertException.expectMessage("Detected use of ENTITY in XML, disabled to prevent XXE/XEE attacks");
        String attackXXE = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                "<!DOCTYPE foo [" +
                "<!ELEMENT foo ANY >" +
                "<!ENTITY xxe SYSTEM \"file:///etc/passwd\" >]>" +
                "<foo>&xxe;</foo>";
        Utils.loadXML(attackXXE);
    }

    @Test
    public void testXMLAttacks_DTD() throws Exception {
        String xmlWithDTD = "<?xml version=\"1.0\"?>" +
                "<!DOCTYPE results [" +
                "<!ELEMENT results (result+)>" +
                "<!ELEMENT result (#PCDATA)>" +
                "]>" +
                "<results>" +
                "<result>test</result>" +
                "</results>";

        Document res2 = Utils.loadXML(xmlWithDTD);
        assertNull(res2);
    }

    @Test
    public void testXMLAttacks_XEE() throws Exception {
        assertException.expect(SecurityException.class);
        assertException.expectMessage("Detected use of ENTITY in XML, disabled to prevent XXE/XEE attacks");

        String attackXEE = "<?xml version=\"1.0\"?>" +
                "<!DOCTYPE results [<!ENTITY harmless \"completely harmless\">]>" +
                "<results>" +
                "<result>This result is &harmless;</result>" +
                "</results>";
        Utils.loadXML(attackXEE);
    }

    @Test
    public void testLoadXML() throws Exception {
        String metadataUnloaded = "<xml><EntityDescriptor>";
        assertNull(Utils.loadXML(metadataUnloaded));

        String metadataInvalid = getFile("metadata/noentity_metadata_settings1.xml");
        assertNotNull(Utils.loadXML(metadataInvalid));

        String metadataOk = getFile("metadata/metadata_settings1.xml");
        assertNotNull(Utils.loadXML(metadataOk));

        String samlResponse = getFile("responses/open_saml_response.xml");
        assertNotNull(Utils.loadXML(samlResponse));
    }

    @Test(expected = SAXParseException.class)
    public void testValidateXML_MalformedXML() throws Exception {
        String metadataUnloaded = "<xml><EntityDescriptor>";
        Document docMetadataUnloaded = Utils.loadXML(metadataUnloaded);
        Utils.validateXML(docMetadataUnloaded, "saml-schema-metadata-2.0.xsd");
    }

    @Test(expected = Error.class)
    public void testValidateXML_MissingMetadata() throws Exception {
        String metadataInvalid = getFile("metadata/noentity_metadata_settings1.xml");
        Document docMetadataInvalid = Utils.loadXML(metadataInvalid);
        Utils.validateXML(docMetadataInvalid, "saml-schema-metadata-2.0.xsd");
    }

    @Test
    public void testValidateXML_metadataExpired() throws Exception {
        String metadataExpired = getFile("metadata/expired_metadata_settings1.xml");
        Document docMetadataExpired = Utils.loadXML(metadataExpired);
        Document doc = Utils.validateXML(docMetadataExpired, "saml-schema-metadata-2.0.xsd");
        assertNotNull(doc);
    }

    @Test
    public void testValidateXML_metadataOK() throws Exception {
        String metadataOk = getFile("metadata/metadata_settings1.xml");
        Document docMetadataOk = Utils.loadXML(metadataOk);
        Document doc2 = Utils.validateXML(docMetadataOk, "saml-schema-metadata-2.0.xsd");
        assertNotNull(doc2);

    }

    @Test
    public void testQuery() throws XPathExpressionException {
        Document dom = loadSignatureDocument("responses/valid_response.xml.base64");

        NodeList assertionNodes = Utils.query(dom, "/samlp:Response/saml:Assertion", null);
        assertEquals(1, assertionNodes.getLength());
        Node assertion = assertionNodes.item(0);
        assertEquals("saml:Assertion", assertion.getNodeName());

        NodeList attributeStatementNodes = Utils.query(dom, "/samlp:Response/saml:Assertion/saml:AttributeStatement", null);
        assertEquals(1, attributeStatementNodes.getLength());
        Node attributeStatement = attributeStatementNodes.item(0);
        assertEquals("saml:AttributeStatement", attributeStatement.getNodeName());

        NodeList attributeStatementNodes2 = Utils.query(dom, "./saml:AttributeStatement", assertion);
        assertEquals(1, attributeStatementNodes2.getLength());
        Node attributeStatement2 = attributeStatementNodes2.item(0);
        assertEquals(attributeStatement, attributeStatement2);

        NodeList signatureResNodes = Utils.query(dom, "/samlp:Response/ds:Signature", null);
        assertEquals(1, signatureResNodes.getLength());
        Node signatureRes = signatureResNodes.item(0);
        assertEquals("ds:Signature", signatureRes.getNodeName());

        NodeList signatureNodes = Utils.query(dom, "/samlp:Response/saml:Assertion/ds:Signature", null);
        assertEquals(1, signatureNodes.getLength());
        Node signature = signatureNodes.item(0);
        assertEquals("ds:Signature", signature.getNodeName());

        NodeList signatureNodes2 = Utils.query(dom, "./ds:Signature", assertion);
        assertEquals(1, signatureNodes2.getLength());
        Node signature2 = signatureNodes2.item(0);
        assertEquals(signature.getTextContent(), signature2.getTextContent());
        assertNotEquals(signatureRes.getTextContent(), signature2.getTextContent());

        NodeList signatureNodes3 = Utils.query(dom, "./ds:SignatureValue", assertion);
        assertEquals(0, signatureNodes3.getLength());

        NodeList signatureNodes4 = Utils.query(dom, "./ds:Signature/ds:SignatureValue", assertion);
        assertEquals(1, signatureNodes4.getLength());

        NodeList signatureNodes5 = Utils.query(dom, ".//ds:SignatureValue", assertion);
        assertEquals(1, signatureNodes5.getLength());
    }

    @Test
    public void testValidateSign_Valid() throws Exception {
        Document dom = loadSignatureDocument("responses/signed_message_response.xml.base64");
        assertNotNull(dom);

        NodeList signatureResNodes = Utils.query(dom, "/samlp:Response/ds:Signature", null);
        assertEquals(1, signatureResNodes.getLength());
        assertTrue(Utils.validateSign(signatureResNodes.item(0), cert));
    }

    @Test(expected = MarshalException.class)
    public void testValidateSign_NonSignatureNode() throws Exception {
        Document dom = loadSignatureDocument("responses/signed_message_response.xml.base64");
        assertNotNull(dom);
        Utils.validateSign(dom.getChildNodes().item(0), cert);
    }

    @Test(expected = MarshalException.class)
    public void testValidateSign_NoKey() throws Exception {
        String fileName = "responses/invalids/no_key.xml.base64";
        Document dom = loadSignatureDocument(fileName);

        NodeList signatureNoKey = Utils.query(dom, "/samlp:Response/saml:Assertion/ds:Signature", null);
        assertEquals(1, signatureNoKey.getLength());
        Utils.validateSign(signatureNoKey.item(0), cert);
    }

    private Document loadSignatureDocument(String fileName) {
        String responseCoded = getFile(fileName);
        Base64 base64 = new Base64();
        byte[] decodedB = base64.decode(responseCoded);
        String response = new String(decodedB);
        return Utils.loadXML(response);
    }

    private String getFile(String fileName) {
        StringBuilder result = new StringBuilder("");

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new RuntimeException("Resource: " + fileName + " not found");
        }
        File file = new File(resource.getFile());

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();

    }

    private static final String certificate =
            "MIICgTCCAeoCCQCbOlrWDdX7FTANBgkqhkiG9w0BAQUFADCBhDELMAkGA1UEBhMCTk8xGDAWBgNVBAgTD0FuZHJlYXM" +
            "gU29sYmVyZzEMMAoGA1UEBxMDRm9vMRAwDgYDVQQKEwdVTklORVRUMRgwFgYDVQQDEw9mZWlkZS5lcmxhbmcubm8xITA" +
            "fBgkqhkiG9w0BCQEWEmFuZHJlYXNAdW5pbmV0dC5ubzAeFw0wNzA2MTUxMjAxMzVaFw0wNzA4MTQxMjAxMzVaMIGEMQs" +
            "wCQYDVQQGEwJOTzEYMBYGA1UECBMPQW5kcmVhcyBTb2xiZXJnMQwwCgYDVQQHEwNGb28xEDAOBgNVBAoTB1VOSU5FVFQ" +
            "xGDAWBgNVBAMTD2ZlaWRlLmVybGFuZy5ubzEhMB8GCSqGSIb3DQEJARYSYW5kcmVhc0B1bmluZXR0Lm5vMIGfMA0GCSq" +
            "GSIb3DQEBAQUAA4GNADCBiQKBgQDivbhR7P516x/S3BqKxupQe0LONoliupiBOesCO3SHbDrl3+q9IbfnfmE04rNuMcP" +
            "sIxB161TdDpIesLCn7c8aPHISKOtPlAeTZSnb8QAu7aRjZq3+PbrP5uW3TcfCGPtKTytHOge/OlJbo078dVhXQ14d1ED" +
            "wXJW1rRXuUt4C8QIDAQABMA0GCSqGSIb3DQEBBQUAA4GBACDVfp86HObqY+e8BUoWQ9+VMQx1ASDohBjwOsg2WykUqRX" +
            "F+dLfcUH9dWR63CtZIKFDbStNomPnQz7nbK+onygwBspVEbnHuUihZq3ZUdmumQqCw4Uvs/1Uvq3orOo/WJVhTyvLgFV" +
            "K2QarQ4/67OZfHd7R+POBXhophSMv1ZOo";

    private static final Certificate cert;

    static {
        try {
            CertificateFactory fty = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(certificate.getBytes()));
            cert = fty.generateCertificate(bais);
        } catch (CertificateException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
