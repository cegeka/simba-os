/*
 * Copyright 2013 Simba Open Source
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
 */
package org.simbasecurity.common.xpath;

import javax.xml.namespace.QName;

public interface WSSEConstants {

    String WSSE_NAMESPACE_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

    String WSSE_PREFIX = "wsse";

    String WSU_NAMESPACE_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

    String WSU_PREFIX = "wsu";

    String SECURITY_PART = "Security";

    String SOAP_ENVELOPE_PREFIX = "env";

    String SOAP_ENVELOPE_URI = "http://schemas.xmlsoap.org/soap/envelope/";

    String SOAP_HEADER_PART = "Header";

    String USECREATED_PART = "Created";

    String USERNAMETOKEN_PART = "UsernameToken";

    QName SECURITY_Q_NAME = new QName(WSSE_NAMESPACE_URI, SECURITY_PART);

}
