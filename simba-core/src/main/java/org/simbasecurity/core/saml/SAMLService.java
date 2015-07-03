package org.simbasecurity.core.saml;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface SAMLService {
    DateFormat SAML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    String getRequest() throws XMLStreamException, IOException;

    String getSSOurl(String relayState) throws XMLStreamException, IOException;

    String getSSOurl() throws XMLStreamException, IOException;

    SAMLResponseHandler getSAMLResponseHandler(String response, String currentURL) throws Exception;
}
