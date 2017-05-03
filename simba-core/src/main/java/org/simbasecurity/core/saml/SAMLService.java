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

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface SAMLService {
    DateFormat SAML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    String createAuthRequest(String authRequestId, Date issueInstant) throws XMLStreamException, IOException;

    String getAuthRequestUrl(String authRequestId, Date issueInstant) throws XMLStreamException, IOException;

    String createLogoutRequest(String logoutRequestId, Date issueInstant, String nameId, String sessionIndex) throws XMLStreamException, IOException;

    String getLogoutRequestUrl(String authRequestId, Date issueInstant, String nameId, String sessionIndex) throws XMLStreamException, IOException;

    SAMLResponseHandler getSAMLResponseHandler(String response, String currentURL) throws Exception;
}
