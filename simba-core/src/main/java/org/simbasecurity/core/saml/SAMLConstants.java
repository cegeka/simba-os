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

public interface SAMLConstants {
    // Value added to the current time in time condition validations
    Integer ALOWED_CLOCK_DRIFT = 180; // 3 min in seconds

    String AUTH_RESPONSE_NODE_NAME = "Response";
    String LOGOUT_RESPONSE_NODE_NAME = "LogoutResponse";

    // NameID Formats
    String NAMEID_EMAIL_ADDRESS = "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress";
    String NAMEID_X509_SUBJECT_NAME = "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName";
    String NAMEID_WINDOWS_DOMAIN_QUALIFIED_NAME = "urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName";
    String NAMEID_KERBEROS = "urn:oasis:names:tc:SAML:2.0:nameid-format:kerberos";
    String NAMEID_ENTITY = "urn:oasis:names:tc:SAML:2.0:nameid-format:entity";
    String NAMEID_TRANSIENT = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
    String NAMEID_PERSISTENT = "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent";
    String NAMEID_ENCRYPTED = "urn:oasis:names:tc:SAML:2.0:nameid-format:encrypted";
    String NAMEID_UNSPECIFIED = "urn:oasis:names:tc:SAML:2.0:nameid-format:unspecified";

    // Attribute Name Formats
    String ATTRNAME_FORMAT_UNSPECIFIED = "urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified";
    String ATTRNAME_FORMAT_URI = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";
    String ATTRNAME_FORMAT_BASIC = "urn:oasis:names:tc:SAML:2.0:attrname-format:basic";

    // Namespaces
    String NS_SAML = "urn:oasis:names:tc:SAML:2.0:assertion";
    String NS_SAMLP = "urn:oasis:names:tc:SAML:2.0:protocol";
    String NS_SOAP = "http://schemas.xmlsoap.org/soap/envelope/";
    String NS_MD = "urn:oasis:names:tc:SAML:2.0:metadata";
    String NS_XS = "http://www.w3.org/2001/XMLSchema";
    String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    String NS_XENC = "http://www.w3.org/2001/04/xmlenc#";
    String NS_DS = "http://www.w3.org/2000/09/xmldsig#";

    // Bindings
    String BINDING_HTTP_POST = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    String BINDING_HTTP_REDIRECT = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
    String BINDING_HTTP_ARTIFACT = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact";
    String BINDING_SOAP = "urn:oasis:names:tc:SAML:2.0:bindings:SOAP";
    String BINDING_DEFLATE = "urn:oasis:names:tc:SAML:2.0:bindings:URL-Encoding:DEFLATE";

    // Auth Context Class
    String AC_UNSPECIFIED = "urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified";
    String AC_PASSWORD = "urn:oasis:names:tc:SAML:2.0:ac:classes:Password";
    String AC_X509 = "urn:oasis:names:tc:SAML:2.0:ac:classes:X509";
    String AC_SMARTCARD = "urn:oasis:names:tc:SAML:2.0:ac:classes:Smartcard";
    String AC_KERBEROS = "urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos";
    String AC_PASSWORD_PROTECTED_TRANSPORT = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";
    String AC_FAS_EID = "urn:be:fedict:iam:fas:citizen:eid";

    // Subject Confirmation
    String CM_BEARER = "urn:oasis:names:tc:SAML:2.0:cm:bearer";
    String CM_HOLDER_KEY = "urn:oasis:names:tc:SAML:2.0:cm:holder-of-key";
    String CM_SENDER_VOUCHES = "urn:oasis:names:tc:SAML:2.0:cm:sender-vouches";

    // Status Codes
    String STATUS_SUCCESS = "urn:oasis:names:tc:SAML:2.0:status:Success";
    String STATUS_REQUESTER = "urn:oasis:names:tc:SAML:2.0:status:Requester";
    String STATUS_RESPONDER = "urn:oasis:names:tc:SAML:2.0:status:Responder";
    String STATUS_VERSION_MISMATCH = "urn:oasis:names:tc:SAML:2.0:status:VersionMismatch";
    String STATUS_NO_PASSIVE = "urn:oasis:names:tc:SAML:2.0:status:NoPassive";
    String STATUS_PARTIAL_LOGOUT = "urn:oasis:names:tc:SAML:2.0:status:PartialLogout";
    String STATUS_PROXY_COUNT_EXCEEDED = "urn:oasis:names:tc:SAML:2.0:status:ProxyCountExceeded";

    // XMLSecurityKey
    String RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
}
