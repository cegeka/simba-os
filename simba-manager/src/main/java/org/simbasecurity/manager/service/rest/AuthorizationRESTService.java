package org.simbasecurity.manager.service.rest;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.api.service.thrift.PolicyDecision;
import org.simbasecurity.common.config.SystemConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("authorization")
public class AuthorizationRESTService implements AuthorizationService.Iface {

    private AuthorizationService.Iface authentorizationService() throws TTransportException {
        THttpClient tHttpClient = new THttpClient(SystemConfiguration.getSimbaServiceURL());
        TProtocol tProtocol = new TJSONProtocol(tHttpClient);
        return new AuthorizationService.Client(tProtocol);
    }

    @RequestMapping("isResourceRuleAllowed")
    @ResponseBody
    public PolicyDecision isResourceRuleAllowed(@JsonBody("username") String username,
                                                @JsonBody("resourcename") String resourceName,
                                                @JsonBody("operation") String operation) {
        return new PolicyDecision(true, Long.MAX_VALUE);
//        try {
//            return authentorizationService().isResourceRuleAllowed(username, resourceName, operation);
//        } catch (TException e) {
//            throw new RuntimeException(e);
//        }
    }

    @RequestMapping("isURLRuleAllowed")
    @ResponseBody
    public PolicyDecision isURLRuleAllowed(@JsonBody("username") String username,
                                           @JsonBody("url") String url,
                                           @JsonBody("method") String method) {
        try {
            return authentorizationService().isURLRuleAllowed(username, url, method);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("isUserInRole")
    @ResponseBody
    public PolicyDecision isUserInRole(@JsonBody("username") String username,
                                       @JsonBody("rolename") String roleName) {
        try {
            return authentorizationService().isUserInRole(username, roleName);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

}
