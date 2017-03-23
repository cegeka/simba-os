package org.simbasecurity.core.chain.eid;

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.session.CheckSessionCommand;
import org.simbasecurity.core.saml.SAMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EIDCheckSessionCommand extends CheckSessionCommand {

    @Autowired private SAMLService samlService;

    @Override
    protected void redirectToLogin(ChainContext context) {
        Map<String, String> parameters = new HashMap<>();
        String authRequestId = context.createLoginMapping().getToken();
        context.redirectWithParameters(getSAMLAuthRequest(authRequestId), parameters);
    }

    private String getSAMLAuthRequest(String authRequestId) {
        try {
            return samlService.getAuthRequestUrl(authRequestId, new Date());
        } catch (XMLStreamException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

