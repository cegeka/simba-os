/*
 * Copyright 2011 Simba Open Source
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
 */
package org.simbasecurity.core.service.manager;

import org.owasp.esapi.errors.ValidationException;
import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.Rule;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RoleRepository;
import org.simbasecurity.core.domain.repository.RuleRepository;
import org.simbasecurity.core.service.manager.assembler.PolicyAssembler;
import org.simbasecurity.core.service.manager.assembler.PolicyDTOAssembler;
import org.simbasecurity.core.service.manager.assembler.RoleDTOAssembler;
import org.simbasecurity.core.service.manager.assembler.RuleDTOAssembler;
import org.simbasecurity.core.service.manager.dto.PolicyDTO;
import org.simbasecurity.core.service.manager.dto.RoleDTO;
import org.simbasecurity.core.service.manager.dto.RuleDTO;
import org.simbasecurity.core.service.manager.web.JsonBody;
import org.simbasecurity.core.service.validation.DTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Set;

import static org.simbasecurity.core.service.manager.assembler.PolicyDTOAssembler.assemble;

@Transactional
@Controller
@RequestMapping("policy")
public class PolicyManagerService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private EntityFilterService filterService;

    @RequestMapping("findAll")
    @ResponseBody
    public Collection<PolicyDTO> findAll() {
        return assemble(filterService.filterPolicies(policyRepository.findAll()));
    }

    @RequestMapping("findRoles")
    @ResponseBody
    public Collection<RoleDTO> findRoles(@RequestBody PolicyDTO policy) {
        return RoleDTOAssembler.assemble(filterService.filterRoles(policyRepository.lookUp(policy).getRoles()));
    }

    @RequestMapping("findRolesNotLinked")
    @ResponseBody
    public Collection<RoleDTO> findRolesNotLinked(@RequestBody PolicyDTO policy) {
        return RoleDTOAssembler.assemble(filterService.filterRoles(roleRepository.findNotLinked(policyRepository.lookUp(policy))));
    }

    @RequestMapping("addRoles")
    @ResponseBody
    public void addRoles(@JsonBody("policy") PolicyDTO policy, @JsonBody("roles") Set<RoleDTO> roles) {
        Policy attachedPolicy = policyRepository.refreshWithOptimisticLocking(policy);
        Collection<Role> attachedRoles = roleRepository.refreshWithOptimisticLocking(roles);

        attachedPolicy.addRoles(attachedRoles);
    }

    @RequestMapping("removeRole")
    @ResponseBody
    public void removeRole(@JsonBody("policy") PolicyDTO policy, @JsonBody("role") RoleDTO role) {
        Policy attachedPolicy = policyRepository.refreshWithOptimisticLocking(policy);
        Role attachedRole = roleRepository.refreshWithOptimisticLocking(role);

        attachedPolicy.removeRole(attachedRole);
    }

    @RequestMapping("addRules")
    @ResponseBody
    public void addRules(@JsonBody("policy") PolicyDTO policy, @JsonBody("rules") Set<RuleDTO> rules) {
        Policy attachedPolicy = policyRepository.refreshWithOptimisticLocking(policy);
        Collection<Rule> attachedRules = ruleRepository.refreshWithOptimisticLocking(rules);

        attachedPolicy.addRules(attachedRules);
    }

    @RequestMapping("findRules")
    @ResponseBody
    public Collection<RuleDTO> findRules(@RequestBody PolicyDTO policy) {
        return RuleDTOAssembler.assemble(policyRepository.lookUp(policy).getRules());
    }

    @RequestMapping("findRulesNotLinked")
    @ResponseBody
    public Collection<RuleDTO> findRulesNotLinked(@RequestBody PolicyDTO policy) {
        return RuleDTOAssembler.assemble(ruleRepository.findNotLinked(policyRepository.lookUp(policy)));
    }

    @RequestMapping("removeRule")
    @ResponseBody
    public void removeRule(@JsonBody("policy") PolicyDTO policy, @JsonBody("rule") RuleDTO rule) {
        Policy attachedPolicy = policyRepository.refreshWithOptimisticLocking(policy);
        Rule attachedRule = ruleRepository.refreshWithOptimisticLocking(rule);

        attachedPolicy.removeRule(attachedRule);
    }

    @RequestMapping("refresh")
    @ResponseBody
    public PolicyDTO refresh(@RequestBody PolicyDTO policy) {
        return assemble(policyRepository.lookUp(policy));
    }

    @RequestMapping("create")
    @ResponseBody
    public PolicyDTO createPolicy(@RequestBody String policyName) throws ValidationException {
        DTOValidator.assertValidString("createRole", policyName);
        if(roleRepository.findByName(policyName) != null) {
            throw new IllegalArgumentException("Policy with name "+policyName+" already exists");
        }
        Policy newPolicy = PolicyAssembler.createPolicy(policyName);
        policyRepository.persist(newPolicy);
        return PolicyDTOAssembler.assemble(newPolicy);
    }
    @RequestMapping("delete")
    @ResponseBody
    public void deletePolicy(@JsonBody("policy") PolicyDTO policy) throws ValidationException {
        DTOValidator.assertValid(policy);
        Policy policyToRemove = policyRepository.lookUp(policy);
        policyRepository.remove(policyToRemove);
    }
}
