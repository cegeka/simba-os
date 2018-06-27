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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.xpath.XPathExpressionException;
import java.security.cert.Certificate;
import java.util.*;

import static org.simbasecurity.core.saml.SAMLConstants.*;

public class SAMLResponseHandlerImpl implements SAMLResponseHandler {

    private static final Logger log = LoggerFactory.getLogger(SAMLResponseHandlerImpl.class);
    private final String currentUrl;
    private final List<Certificate> certificates;

    private Document document;

    private Element rootElement;
    private StringBuilder error;
    private String audienceUrl;


    /**
     * Constructor to have a Response object full builded and ready to validate the saml response
     *
     * @param certificates The IDP certificates to use
     * @param response    SAML Response on string format
     * @param currentURL  URL of the current host + current view
     */
    public SAMLResponseHandlerImpl(List<Certificate> certificates, String response, String currentURL) throws Exception {
        this.certificates = certificates;
        loadXmlFromBase64(response);
        this.currentUrl = currentURL;
        this.error = new StringBuilder();
    }

    public void loadXmlFromBase64(String responseStr) throws Exception {
        Base64 base64 = new Base64();
        byte[] decodedB = base64.decode(responseStr);
        this.document = Utils.loadXML(new String(decodedB));
        if (this.document == null) {
            throw new Exception("SAML Response could not be processed");
        }
        rootElement = document.getDocumentElement();
        rootElement.normalize();
    }

    @Override
    public boolean isLogoutResponse() {
        return LOGOUT_RESPONSE_NODE_NAME.equals(document.getDocumentElement().getLocalName());
    }

    @Override
    public boolean isAuthenticationResponse() {
        return AUTH_RESPONSE_NODE_NAME.equals(document.getDocumentElement().getLocalName());
    }

    // isValid() function should be called to make basic security checks to responses.
    @Override
    public boolean isValid(String... requestId) {
        try {
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            if (this.currentUrl == null || this.currentUrl.isEmpty()) {
                throw new Exception("The URL of the current host was not established");
            }

            // Check SAML version
            if (!rootElement.getAttribute("Version").equals("2.0")) {
                throw new Exception("Unsupported SAML Version.");
            }

            // Check ID in the response
            if (!rootElement.hasAttribute("ID")) {
                throw new Exception("Missing ID attribute on SAML Response.");
            }

            checkStatus();

            if (!this.validateNumAssertions()) {
                throw new Exception("SAML Response must contain 1 Assertion.");
            }

            NodeList signNodes = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            ArrayList<String> signedElements = new ArrayList<>();
            for (int i = 0; i < signNodes.getLength(); i++) {
                signedElements.add(signNodes.item(i).getParentNode().getLocalName());
            }
            if (!signedElements.isEmpty()) {
                if (!this.validateSignedElements(signedElements)) {
                    throw new Exception("Found an unexpected Signature Element. SAML Response rejected");
                }
            }

            Document res = Utils.validateXML(this.document, "saml-schema-protocol-2.0.xsd");

            if (res == null) {
                throw new Exception("Invalid SAML Response. Not match the saml-schema-protocol-2.0.xsd");
            }

            if (rootElement.hasAttribute("InResponseTo")) {
                String responseInResponseTo = document.getDocumentElement().getAttribute("InResponseTo");
                if (requestId.length > 0 && responseInResponseTo.compareTo(requestId[0]) != 0) {
                    throw new Exception("The InResponseTo of the Response: " + responseInResponseTo + ", does not match the ID of the AuthNRequest sent by the SP: " + requestId[0]);
                }
            }

            // Validate Assertion timestamps
            if (!this.validateTimestamps()) {
                throw new Exception("Timing issues (please check your clock settings)");
            }

            // EncryptedAttributes are not supported
            NodeList encryptedAttributeNodes = this.queryAssertion("/saml:AttributeStatement/saml:EncryptedAttribute");
            if (encryptedAttributeNodes.getLength() > 0) {
                throw new Exception("There is an EncryptedAttribute in the Response and this SP not support them");
            }

            // Check destination
//          TODO: lenneh: bktis: currentUrl is http:// and the destination is https://
//            if (rootElement.hasAttribute("Destination")) {
//                String destinationUrl = rootElement.getAttribute("Destination");
//                if (destinationUrl != null) {
//                    if (!destinationUrl.equals(currentUrl)) {
//                        throw new Exception("The response was received at " + currentUrl + " instead of " + destinationUrl);
//                    }
//                }
//            }

            // Check Audience
//          TODO: lenneh: bktis: currentUrl is http:// and audienceUrl is https://
//            Set<String> validAudiences = this.getAudiences();
//
//            if (validAudiences.isEmpty() || !this.audienceUrl.equals(currentUrl)) {
//                throw new Exception(this.audienceUrl + " is not a valid audience for this Response");
//            }

            // Check the issuers
            Set<String> issuers = this.getIssuers();
            for (String issuer : issuers) {
                if (issuer.isEmpty()) {
                    throw new Exception("Invalid issuer in the Assertion/Response");
                }
            }

            // Check the session Expiration
            Calendar sessionExpiration = this.getSessionNotOnOrAfter();
            if (sessionExpiration != null) {
                if (now.equals(sessionExpiration) || now.after(sessionExpiration)) {
                    throw new Exception("The attributes have expired, based on the SessionNotOnOrAfter of the AttributeStatement of this Response");
                }
            }

            // Check SubjectConfirmation, at least one SubjectConfirmation must be valid
            boolean validSubjectConfirmation = true;
            NodeList subjectConfirmationNodes = this.queryAssertion("/saml:Subject/saml:SubjectConfirmation");
            for (int i = 0; i < subjectConfirmationNodes.getLength(); i++) {
                Node scn = subjectConfirmationNodes.item(i);

                Node method = scn.getAttributes().getNamedItem("Method");
                if (method != null && !method.getNodeValue().equals(SAMLConstants.CM_BEARER)) {
                    continue;
                }

                NodeList subjectConfirmationDataNodes = scn.getChildNodes();
                for (int c = 0; c < subjectConfirmationDataNodes.getLength(); c++) {

                    Node subjectConfirmationData = subjectConfirmationDataNodes.item(c);
                    if (subjectConfirmationData.getNodeType() == Node.ELEMENT_NODE
                        && subjectConfirmationData.getLocalName().equals("SubjectConfirmationData")) {

//                      TODO: lenneh: bktis: currentUrl is http:// and the recipient is https://
//                        Node recipient = subjectConfirmationData.getAttributes().getNamedItem("Recipient");
//                        if (recipient != null && !recipient.getNodeValue().equals(currentUrl)) {
//                            validSubjectConfirmation = false;
//                        }

                        Node notOnOrAfter = subjectConfirmationData.getAttributes().getNamedItem("NotOnOrAfter");
                        if (notOnOrAfter != null) {
                            Calendar noa = javax.xml.bind.DatatypeConverter.parseDateTime(notOnOrAfter.getNodeValue());
                            if (now.equals(noa) || now.after(noa)) {
                                validSubjectConfirmation = false;
                            }
                        }

                        Node notBefore = subjectConfirmationData.getAttributes().getNamedItem("NotBefore");
                        if (notBefore != null) {
                            Calendar nb = javax.xml.bind.DatatypeConverter.parseDateTime(notBefore.getNodeValue());
                            if (now.before(nb)) {
                                validSubjectConfirmation = false;
                            }
                        }
                    }
                }
            }
            if (!validSubjectConfirmation) {
                throw new Exception("A valid SubjectConfirmation was not found on this Response");
            }

            if (signedElements.isEmpty()) {
                throw new Exception("No Signature found. SAML Response rejected");
            } else {
                certificates.stream()
                        .filter(certificate -> {
                            try {
                                return Utils.validateSign(signNodes.item(0), certificate);
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .findFirst()
                        .orElseThrow(() -> new Exception("Signature validation failed. SAML Response rejected"));
            }
            return true;
        } catch (Error e) {
            error.append(e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            error.append(e.getMessage());
            return false;
        }
    }

    public String getNameId() throws Exception {
        NodeList nodes = document.getElementsByTagNameNS(SAMLConstants.NS_SAML, "NameID");
        if (nodes.getLength() == 0) {
            throw new Exception("No name id found in Document.");
        }
        return nodes.item(0).getTextContent();
    }

    @Override
    public String getInResponseTo() {
        return rootElement.hasAttribute("InResponseTo")
            ? rootElement.getAttribute("InResponseTo").substring(1)
            : null;
    }

    @Override
    public String getAttribute(String name) {
        HashMap<String, ArrayList<String>> attributes = getAttributes();
        ArrayList<String> attributeValues = attributes.get(name);
        return attributeValues != null && !attributeValues.isEmpty() ? attributeValues.get(0) : null;
    }

    @Override
    public String getMessageID() {
        return rootElement.getAttribute("ID");
    }

    @Override
    public String getIssueInstant() {
        return rootElement.getAttribute("IssueInstant");
    }

    public HashMap<String, ArrayList<String>> getAttributes() {
        HashMap<String, ArrayList<String>> attributes = new HashMap<>();
        NodeList nodes = document.getElementsByTagNameNS(SAMLConstants.NS_SAML, "Attribute");

        if (nodes.getLength() != 0) {
            for (int i = 0; i < nodes.getLength(); i++) {
                NamedNodeMap attrName = nodes.item(i).getAttributes();
                String attName = attrName.getNamedItem("Name").getNodeValue();
                NodeList children = nodes.item(i).getChildNodes();

                ArrayList<String> attrValues = new ArrayList<>();
                for (int j = 0; j < children.getLength(); j++) {
                    attrValues.add(children.item(j).getTextContent());
                }
                attributes.put(attName, attrValues);
            }
        } else {
            return null;
        }
        return attributes;
    }

    /**
     * Checks if the Status is success
     *
     * @throws Exception If status is not success
     */
    private Map<String, String> checkStatus() throws Exception {
        Map<String, String> status = Utils.getStatus(document);
        if (status.containsKey("code") && !status.get("code").equals(SAMLConstants.STATUS_SUCCESS)) {
            String statusExceptionMsg = "The status code of the Response was not Success, was " +
                    status.get("code").substring(status.get("code").lastIndexOf(':') + 1);
            if (status.containsKey("msg")) {
                statusExceptionMsg += " -> " + status.containsKey("msg");
            }
            throw new Exception(statusExceptionMsg);
        }

        return status;

    }

    /**
     * Gets the audiences.
     *
     * @return The audiences of the response
     * @throws XPathExpressionException
     */
    public Set<String> getAudiences() throws XPathExpressionException {
        Set<String> audiences = new LinkedHashSet<>();

        NodeList entries = this.queryAssertion("/saml:Conditions/saml:AudienceRestriction/saml:Audience");

        if (entries.getLength() > 0) {
            this.audienceUrl = entries.item(0).getChildNodes().item(0).getNodeValue();
        }

        for (int i = 0; i < entries.getLength(); i++) {
            String value = entries.item(i).getTextContent().trim();
            if (!value.isEmpty()) {
                audiences.add(value);
            }
        }
        return audiences;
    }

    /**
     * Gets the Issuers (from Response and Assertion).
     *
     * @return The issuers of the assertion/response
     * @throws XPathExpressionException
     */
    public Set<String> getIssuers() throws XPathExpressionException {
        Set<String> issuers = new LinkedHashSet<>();

        NodeList responseIssuer = this.queryAssertion("/samlp:Response/saml:Issuer");
        if (responseIssuer.getLength() == 1) {
            issuers.add(responseIssuer.item(0).getTextContent());
        }

        NodeList assertionIssuer = this.queryAssertion("/saml:Issuer");
        if (assertionIssuer.getLength() == 1) {
            issuers.add(assertionIssuer.item(0).getTextContent());
        }

        return issuers;
    }

    /**
     * Gets the SessionNotOnOrAfter from the AuthnStatement.
     * Could be used to set the local session expiration
     *
     * @return DateTime|null The SessionNotOnOrAfter value
     * @throws XPathExpressionException
     */
    public Calendar getSessionNotOnOrAfter() throws XPathExpressionException {
        String notOnOrAfter;
        NodeList entries = this.queryAssertion("/saml:AuthnStatement[@SessionNotOnOrAfter]");
        if (entries.getLength() > 0) {
            notOnOrAfter = entries.item(0).getAttributes().getNamedItem("SessionNotOnOrAfter").getNodeValue();
            return javax.xml.bind.DatatypeConverter.parseDateTime(notOnOrAfter);
        }
        return null;
    }

    /**
     * Verifies that the document only contains a single Assertion (encrypted or not).
     *
     * @return true if the document passes.
     */
    private boolean validateNumAssertions() {
        NodeList assertionNodes = this.document.getElementsByTagNameNS(SAMLConstants.NS_SAML, "Assertion");
        return assertionNodes != null && assertionNodes.getLength() == 1;
    }

    /**
     * Verifies that the document has the expected signed nodes.
     *
     * @return true if is valid
     */
    private boolean validateSignedElements(ArrayList<String> signedElements) {
        if (signedElements.size() > 2) {
            return false;
        }
        Map<String, Integer> occurrences = new HashMap<>();
        for (String e : signedElements) {
            if (occurrences.containsKey(e)) {
                occurrences.put(e, occurrences.get(e) + 1);
            } else {
                occurrences.put(e, 1);
            }
        }

        return !((occurrences.containsKey("Response") && occurrences.get("Response") > 1) ||
                (occurrences.containsKey("Assertion") && occurrences.get("Assertion") > 1) ||
                !occurrences.containsKey("Response") && !occurrences.containsKey("Assertion"));
    }

    /**
     * Verifies that the document is still valid according Conditions Element.
     *
     * @return true if still valid
     */
    private boolean validateTimestamps() {
        NodeList timestampNodes = document.getElementsByTagNameNS("*", "Conditions");
        if (timestampNodes.getLength() != 0) {
            for (int i = 0; i < timestampNodes.getLength(); i++) {
                NamedNodeMap attrName = timestampNodes.item(i).getAttributes();
                Node nbAttribute = attrName.getNamedItem("NotBefore");
                Node naAttribute = attrName.getNamedItem("NotOnOrAfter");
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                log.debug("now :" + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));
                // validate NotOnOrAfter using UTC
                if (naAttribute != null) {
                    final Calendar notOnOrAfterDate = javax.xml.bind.DatatypeConverter.parseDateTime(naAttribute.getNodeValue());
                    log.debug("notOnOrAfterDate :" + notOnOrAfterDate.get(Calendar.HOUR_OF_DAY) + ":" + notOnOrAfterDate.get(Calendar.MINUTE) + ":" + notOnOrAfterDate.get(Calendar.SECOND));
                    if (now.equals(notOnOrAfterDate) || now.after(notOnOrAfterDate)) {
                        return false;
                    }
                }
                // validate NotBefore using UTC
                if (nbAttribute != null) {
                    final Calendar notBeforeDate = javax.xml.bind.DatatypeConverter.parseDateTime(nbAttribute.getNodeValue());
                    log.debug("notBeforeDate :" + notBeforeDate.get(Calendar.HOUR_OF_DAY) + ":" + notBeforeDate.get(Calendar.MINUTE) + ":" + notBeforeDate.get(Calendar.SECOND));
                    if (now.before(notBeforeDate)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String getError() {
        if (error != null)
            return error.toString();
        return "";
    }

    /**
     * Extracts a node from the DOMDocument (Assertion).
     *
     * @param assertionXpath $assertionXpath Xpath Expresion
     * @return DOMNodeList The queried node
     * @throws XPathExpressionException
     */
    private NodeList queryAssertion(String assertionXpath) throws XPathExpressionException {

        String nameQuery;
        String signatureQuery = "/samlp:Response/saml:Assertion/ds:Signature/ds:SignedInfo/ds:Reference";
        NodeList nodeList = Utils.query(this.document, signatureQuery, null);
        if (nodeList.getLength() > 0) {
            Node assertionReferenceNode = nodeList.item(0);
            String id = assertionReferenceNode.getAttributes().getNamedItem("URI").getNodeValue().substring(1);
            nameQuery = "/samlp:Response/saml:Assertion[@ID='" + id + "']" + assertionXpath;

        } else {  // is the response signed as a whole?
            signatureQuery = "/samlp:Response/ds:Signature/ds:SignedInfo/ds:Reference";
            nodeList = Utils.query(this.document, signatureQuery, null);
            if (nodeList.getLength() > 0) {
                Node assertionReferenceNode = nodeList.item(0);
                String id = assertionReferenceNode.getAttributes().getNamedItem("URI").getNodeValue().substring(1);
                nameQuery = "/samlp:Response[@ID='" + id + "']/saml:Assertion" + assertionXpath;
            } else {
                nameQuery = "/samlp:Response/saml:Assertion" + assertionXpath;
            }
        }
        return Utils.query(this.document, nameQuery, null);
    }


}
