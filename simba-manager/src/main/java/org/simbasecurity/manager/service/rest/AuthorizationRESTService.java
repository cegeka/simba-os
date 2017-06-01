package org.simbasecurity.manager.service.rest;

import org.apache.thrift.TException;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.manager.service.rest.dto.PolicyDecisionDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("authorization")
public class AuthorizationRESTService extends BaseRESTService<AuthorizationService.Client> {

    public AuthorizationRESTService() {
        super(new AuthorizationService.Client.Factory(), SimbaConfiguration.getSimbaAuthorizationURL());
    }

    @RequestMapping("isResourceRuleAllowed")
    @ResponseBody
    public PolicyDecisionDTO isResourceRuleAllowed(@JsonBody("username") String username,
                                                   @JsonBody("resourcename") String resourceName,
                                                   @JsonBody("operation") String operation) {
        try {
            return new PolicyDecisionDTO(getServiceClient().isResourceRuleAllowed(username, resourceName, operation));
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("isURLRuleAllowed")
    @ResponseBody
    public PolicyDecisionDTO isURLRuleAllowed(@JsonBody("username") String username,
                                              @JsonBody("url") String url,
                                              @JsonBody("method") String method) {
        try {
            return new PolicyDecisionDTO(getServiceClient().isURLRuleAllowed(username, url, method));
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("isUserInRole")
    @ResponseBody
    public PolicyDecisionDTO isUserInRole(@JsonBody("username") String username,
                                          @JsonBody("rolename") String roleName) {
        try {
            return new PolicyDecisionDTO(getServiceClient().isUserInRole(username, roleName));
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }
}
