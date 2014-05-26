package org.simbasecurity.core.service.thrift;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.simbasecurity.api.service.thrift.AuthorizationService;

public class AuthorizationServlet extends SpringTServlet {

    public AuthorizationServlet() {
        super(AuthorizationService.Processor.class, "authorizationService", new TJSONProtocol.Factory());
    }

}
