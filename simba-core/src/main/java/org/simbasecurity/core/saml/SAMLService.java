package org.simbasecurity.core.saml;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface SAMLService {
    DateFormat SAML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    String createAuthRequest(String authRequestId) throws XMLStreamException, IOException;

    String getAuthRequestUrl(String authRequestId) throws XMLStreamException, IOException;

    String createLogoutRequest(String authRequestId) throws XMLStreamException, IOException;

    String getLogoutRequestUrl(String authRequestId) throws XMLStreamException, IOException;

    SAMLResponseHandler getSAMLResponseHandler(String response, String currentURL) throws Exception;
}
