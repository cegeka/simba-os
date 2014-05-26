package org.simbasecurity.core.service.thrift;

import org.apache.thrift.protocol.TJSONProtocol;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;

public class AuthenticationServlet extends SpringTServlet {

    public AuthenticationServlet() {
        super(AuthenticationFilterService.Processor.class, "authenticationFilterService", new TJSONProtocol.Factory());
    }

}
