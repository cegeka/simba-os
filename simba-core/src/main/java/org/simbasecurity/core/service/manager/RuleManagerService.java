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
package org.simbasecurity.core.service.manager;

import static org.simbasecurity.core.service.manager.assembler.PolicyDTOAssembler.*;

import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.Rule;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.RuleRepository;
import org.simbasecurity.core.service.manager.assembler.RuleDTOAssembler;
import org.simbasecurity.core.service.manager.dto.PolicyDTO;
import org.simbasecurity.core.service.manager.dto.RuleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Transactional
@Controller
@RequestMapping("rule")
public class RuleManagerService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @RequestMapping("findForRule")
    @ResponseBody
    public PolicyDTO findPolicyFor(@RequestBody RuleDTO rule) {
        return assemble(policyRepository.find(ruleRepository.lookUp(rule)));
    }

    @RequestMapping("refresh")
    @ResponseBody
    public RuleDTO refresh(@RequestBody RuleDTO rule) {
        return RuleDTOAssembler.assemble(ruleRepository.lookUp(rule));
    }

    @RequestMapping("setPolicy")
    public void setPolicy(@RequestBody RuleDTO rule, @RequestBody PolicyDTO policy) {
        Rule attachedRule = ruleRepository.refreshWithOptimisticLocking(rule);
        Policy attachedPolicy = policy == null ? null : policyRepository.refreshWithOptimisticLocking(policy);

        attachedRule.setPolicy(attachedPolicy);
    }
}
