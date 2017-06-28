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

import org.simbasecurity.api.service.thrift.PolicyService;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.manager.service.rest.dto.PolicyDTO;
import org.simbasecurity.manager.service.rest.dto.RoleDTO;
import org.simbasecurity.manager.service.rest.dto.RuleDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Set;

import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;
import static org.simbasecurity.manager.service.rest.assembler.DTOAssembler.*;

@Controller
@RequestMapping("policy")
public class PolicyRESTService extends BaseRESTService<PolicyService.Client> {

    public PolicyRESTService() {
        super(new PolicyService.Client.Factory(), SimbaConfiguration.getPolicyServiceURL());
    }

    @RequestMapping("findAll")
    @ResponseBody
    public Collection<PolicyDTO> findAll(@CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return list($(() -> cl(ssoToken).findAll()));
    }

    @RequestMapping("findRoles")
    @ResponseBody
    public Collection<RoleDTO> findRoles(@RequestBody PolicyDTO policy,
                                         @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return list($(() -> cl(ssoToken).findRoles(assemble(policy))));
    }

    @RequestMapping("findRolesNotLinked")
    @ResponseBody
    public Collection<RoleDTO> findRolesNotLinked(@RequestBody PolicyDTO policy,
                                                  @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return list($(() -> cl(ssoToken).findRolesNotLinked(assemble(policy))));
    }

    @RequestMapping("addRoles")
    @ResponseBody
    public void addRoles(@JsonBody("policy") PolicyDTO policy, @JsonBody("roles") Set<RoleDTO> roles,
                         @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).addRoles(assemble(policy), set(roles)));
    }

    @RequestMapping("removeRole")
    @ResponseBody
    public void removeRole(@JsonBody("policy") PolicyDTO policy, @JsonBody("role") RoleDTO role,
                           @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).removeRole(assemble(policy), assemble(role)));
    }

    @RequestMapping("addRules")
    @ResponseBody
    public void addRules(@JsonBody("policy") PolicyDTO policy, @JsonBody("rules") Set<RuleDTO> rules,
                         @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).addRules(assemble(policy), set(rules)));
    }

    @RequestMapping("findRules")
    @ResponseBody
    public Collection<RuleDTO> findRules(@RequestBody PolicyDTO policy,
                                         @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return list($(() -> cl(ssoToken).findRules(assemble(policy))));
    }

    @RequestMapping("findRulesNotLinked")
    @ResponseBody
    public Collection<RuleDTO> findRulesNotLinked(@RequestBody PolicyDTO policy,
                                                  @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return list($(() -> cl(ssoToken).findRulesNotLinked(assemble(policy))));
    }

    @RequestMapping("removeRule")
    @ResponseBody
    public void removeRule(@JsonBody("policy") PolicyDTO policy, @JsonBody("rule") RuleDTO rule,
                           @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).removeRule(assemble(policy), assemble(rule)));
    }

    @RequestMapping("refresh")
    @ResponseBody
    public PolicyDTO refresh(@RequestBody PolicyDTO policy,
                             @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return assemble($(() -> cl(ssoToken).refresh(assemble(policy))));
    }

    @RequestMapping("create")
    @ResponseBody
    public PolicyDTO createPolicy(@RequestBody String policyName,
                                  @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        return assemble($(() -> cl(ssoToken).createPolicy(policyName)));
    }

    @RequestMapping("delete")
    @ResponseBody
    public void deletePolicy(@JsonBody("policy") PolicyDTO policy,
                             @CookieValue(value = SIMBA_SSO_TOKEN, required = false) String ssoToken) {
        $(() -> cl(ssoToken).deletePolicy(assemble(policy)));
    }
}
