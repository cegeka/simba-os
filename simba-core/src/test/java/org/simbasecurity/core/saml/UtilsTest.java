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
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import javax.xml.crypto.MarshalException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UtilsTest {

    /**
     * Tests the loadXML method of the com.onelogin.saml.Utils
     *
     * @covers Utils.loadXML
     */
    @Test
    public void testXMLAttacks() throws Exception{
        String attackXXE = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                           + "<!DOCTYPE foo ["
                           + "<!ELEMENT foo ANY >"
                           + "<!ENTITY xxe SYSTEM \"file:///etc/passwd\" >]>"
                           + "<foo>&xxe;</foo>";

        assertThatThrownBy(() -> Utils.loadXML(attackXXE)).isInstanceOf(SecurityException.class)
                                                          .hasMessage("Detected use of ENTITY in XML, disabled to prevent XXE/XEE attacks");

        String xmlWithDTD = "<?xml version=\"1.0\"?>"
                            + "<!DOCTYPE results ["
                            + "<!ELEMENT results (result+)>"
                            + "<!ELEMENT result (#PCDATA)>"
                            + "]>"
                            + "<results>"
                            + "<result>test</result>"
                            + "</results>";

        assertThat(Utils.loadXML(xmlWithDTD)).isNull();

        String attackXEE = "<?xml version=\"1.0\"?>"
                           + "<!DOCTYPE results [<!ENTITY harmless \"completely harmless\">]>"
                           + "<results>"
                           + "<result>This result is &harmless;</result>"
                           + "</results>";

        assertThatThrownBy(() -> Utils.loadXML(attackXEE)).isInstanceOf(SecurityException.class)
                                                          .hasMessage("Detected use of ENTITY in XML, disabled to prevent XXE/XEE attacks");
    }

    /**
     * Tests the loadXML method of the com.onelogin.saml.Utils
     *
     * @covers Utils.loadXML
     */
    @Test
    public void testLoadXML() throws Exception {
        String metadataUnloaded = "<xml><EntityDescriptor>";

        assertThat(Utils.loadXML(metadataUnloaded)).isNull();

        String metadataInvalid = getFile("metadata/noentity_metadata_settings1.xml");
        assertThat(Utils.loadXML(metadataInvalid)).isInstanceOf(Document.class);

        String metadataOk = getFile("metadata/metadata_settings1.xml");
        assertThat(Utils.loadXML(metadataOk)).isInstanceOf(Document.class);

        String samlResponse = getFile("responses/open_saml_response.xml");
        assertThat(Utils.loadXML(samlResponse)).isInstanceOf(Document.class);
    }

    /**
     * Tests the validateXML method of the com.onelogin.saml.Utils
     *
     * @covers Utils.validateXML
     */
    @Test
    public void testValidateXML() throws Exception {
        String metadataUnloaded = "<xml><EntityDescriptor>";
        Document docMetadataUnloaded = Utils.loadXML(metadataUnloaded);
        assertThatThrownBy(() -> Utils.validateXML(docMetadataUnloaded, "saml-schema-metadata-2.0.xsd"))
                .isInstanceOf(SAXParseException.class);

        String metadataInvalid = getFile("metadata/noentity_metadata_settings1.xml");
        Document docMetadataInvalid = Utils.loadXML(metadataInvalid);
        assertThatThrownBy(() -> Utils.validateXML(docMetadataInvalid, "saml-schema-metadata-2.0.xsd"))
                .isInstanceOf(Error.class);

        String metadataExpired = getFile("metadata/expired_metadata_settings1.xml");
        Document docMetadataExpired = Utils.loadXML(metadataExpired);
        Document doc = Utils.validateXML(docMetadataExpired, "saml-schema-metadata-2.0.xsd");
        assertThat(doc).isInstanceOf(Document.class);

        String metadataOk = getFile("metadata/metadata_settings1.xml");
        Document docMetadataOk = Utils.loadXML(metadataOk);
        Document doc2 = Utils.validateXML(docMetadataOk, "saml-schema-metadata-2.0.xsd");
        assertThat(doc2).isInstanceOf(Document.class);
    }

    /**
     * Tests the query method of the com.onelogin.saml.Utils
     * <p>
     * covers Utils.query
     */
    @Test
    public void testQuery() throws Exception {
        String responseCoded = getFile("responses/valid_response.xml.base64");
        Base64 base64 = new Base64();
        byte[] decodedB = base64.decode(responseCoded);
        String response = new String(decodedB);
        Document dom = Utils.loadXML(response);

        NodeList assertionNodes = Utils.query(dom, "/samlp:Response/saml:Assertion", null);
        assertThat(assertionNodes.getLength()).isEqualTo(1);
        Node assertion = assertionNodes.item(0);
        assertThat(assertion.getNodeName()).isEqualTo("saml:Assertion");

        NodeList attributeStatementNodes = Utils.query(dom, "/samlp:Response/saml:Assertion/saml:AttributeStatement", null);
        assertThat(attributeStatementNodes.getLength()).isEqualTo(1);
        Node attributeStatement = attributeStatementNodes.item(0);
        assertThat(attributeStatement.getNodeName()).isEqualTo("saml:AttributeStatement");

        NodeList attributeStatementNodes2 = Utils.query(dom, "./saml:AttributeStatement", assertion);
        assertThat(attributeStatementNodes2.getLength()).isEqualTo(1);
        Node attributeStatement2 = attributeStatementNodes2.item(0);
        assertThat(attributeStatement2).isEqualTo(attributeStatement);

        NodeList signatureResNodes = Utils.query(dom, "/samlp:Response/ds:Signature", null);
        assertThat(signatureResNodes.getLength()).isEqualTo(1);
        Node signatureRes = signatureResNodes.item(0);
        assertThat(signatureRes.getNodeName()).isEqualTo("ds:Signature");

        NodeList signatureNodes = Utils.query(dom, "/samlp:Response/saml:Assertion/ds:Signature", null);
        assertThat(signatureNodes.getLength()).isEqualTo(1);
        Node signature = signatureNodes.item(0);
        assertThat(signature.getNodeName()).isEqualTo("ds:Signature");

        NodeList signatureNodes2 = Utils.query(dom, "./ds:Signature", assertion);
        assertThat(signatureNodes2.getLength()).isEqualTo(1);
        Node signature2 = signatureNodes2.item(0);
        assertThat(signature2.getTextContent()).isEqualTo(signature.getTextContent());
        assertThat(signature2.getTextContent()).isNotEqualTo(signatureRes.getTextContent());

        NodeList signatureNodes3 = Utils.query(dom, "./ds:SignatureValue", assertion);
        assertThat(signatureNodes3.getLength()).isEqualTo(0);

        NodeList signatureNodes4 = Utils.query(dom, "./ds:Signature/ds:SignatureValue", assertion);
        assertThat(signatureNodes4.getLength()).isEqualTo(1);

        NodeList signatureNodes5 = Utils.query(dom, ".//ds:SignatureValue", assertion);
        assertThat(signatureNodes5.getLength()).isEqualTo(1);
    }

    /**
     * Tests the validateSign method of the com.onelogin.saml.Utils
     */
    @Test
    public void testValidateSign() throws Exception {
        String certificate = "MIICgTCCAeoCCQCbOlrWDdX7FTANBgkqhkiG9w0BAQUFADCBhDELMAkGA1UEBhMCTk8xGDAWBgNVBAgTD0FuZHJlYXM"
                             + "gU29sYmVyZzEMMAoGA1UEBxMDRm9vMRAwDgYDVQQKEwdVTklORVRUMRgwFgYDVQQDEw9mZWlkZS5lcmxhbmcubm8xITA"
                             + "fBgkqhkiG9w0BCQEWEmFuZHJlYXNAdW5pbmV0dC5ubzAeFw0wNzA2MTUxMjAxMzVaFw0wNzA4MTQxMjAxMzVaMIGEMQs"
                             + "wCQYDVQQGEwJOTzEYMBYGA1UECBMPQW5kcmVhcyBTb2xiZXJnMQwwCgYDVQQHEwNGb28xEDAOBgNVBAoTB1VOSU5FVFQ"
                             + "xGDAWBgNVBAMTD2ZlaWRlLmVybGFuZy5ubzEhMB8GCSqGSIb3DQEJARYSYW5kcmVhc0B1bmluZXR0Lm5vMIGfMA0GCSq"
                             + "GSIb3DQEBAQUAA4GNADCBiQKBgQDivbhR7P516x/S3BqKxupQe0LONoliupiBOesCO3SHbDrl3+q9IbfnfmE04rNuMcP"
                             + "sIxB161TdDpIesLCn7c8aPHISKOtPlAeTZSnb8QAu7aRjZq3+PbrP5uW3TcfCGPtKTytHOge/OlJbo078dVhXQ14d1ED"
                             + "wXJW1rRXuUt4C8QIDAQABMA0GCSqGSIb3DQEBBQUAA4GBACDVfp86HObqY+e8BUoWQ9+VMQx1ASDohBjwOsg2WykUqRX"
                             + "F+dLfcUH9dWR63CtZIKFDbStNomPnQz7nbK+onygwBspVEbnHuUihZq3ZUdmumQqCw4Uvs/1Uvq3orOo/WJVhTyvLgFV"
                             + "K2QarQ4/67OZfHd7R+POBXhophSMv1ZOo";
        CertificateFactory fty = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(certificate.getBytes()));
        Certificate cert = fty.generateCertificate(bais);

        String responseCoded = getFile("responses/signed_message_response.xml.base64");
        Base64 base64 = new Base64();
        byte[] decodedB = base64.decode(responseCoded);
        String response = new String(decodedB);
        Document dom = Utils.loadXML(response);

        NodeList signatureResNodes = Utils.query(dom, "/samlp:Response/ds:Signature", null);
        assertThat(signatureResNodes.getLength()).isEqualTo(1);
        assertThat(Utils.validateSign(signatureResNodes.item(0), cert)).isTrue();

        assertThatThrownBy(() -> Utils.validateSign(dom.getChildNodes().item(0), cert)).isInstanceOf(MarshalException.class)
                                                                                             .hasMessageContaining("invalid Signature");

        responseCoded = getFile("responses/invalids/no_key.xml.base64");
        base64 = new Base64();
        decodedB = base64.decode(responseCoded);
        response = new String(decodedB);

        NodeList signatureNoKey = Utils.query(Utils.loadXML(response), "/samlp:Response/saml:Assertion/ds:Signature", null);
        assertThat(signatureNoKey.getLength()).isEqualTo(1);

        assertThatThrownBy(() -> Utils.validateSign(signatureNoKey.item(0), cert)).isInstanceOf(MarshalException.class);
    }

    private String getFile(String fileName) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        StringBuilder result = new StringBuilder("");
        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }
}