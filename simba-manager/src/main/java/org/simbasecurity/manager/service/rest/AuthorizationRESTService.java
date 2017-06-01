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
package org.simbasecurity.manager.service.rest;

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
        return new PolicyDecisionDTO($(() -> cl().isResourceRuleAllowed(username, resourceName, operation)));
    }

    @RequestMapping("isURLRuleAllowed")
    @ResponseBody
    public PolicyDecisionDTO isURLRuleAllowed(@JsonBody("username") String username,
                                              @JsonBody("url") String url,
                                              @JsonBody("method") String method) {
        return new PolicyDecisionDTO($(() -> cl().isURLRuleAllowed(username, url, method)));
    }

    @RequestMapping("isUserInRole")
    @ResponseBody
    public PolicyDecisionDTO isUserInRole(@JsonBody("username") String username,
                                          @JsonBody("rolename") String roleName) {
        return new PolicyDecisionDTO($(() -> cl().isUserInRole(username, roleName)));
    }
}
