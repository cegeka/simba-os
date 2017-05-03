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

import org.simbasecurity.core.domain.Condition;
import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.ConditionRepository;
import org.simbasecurity.core.domain.repository.PolicyRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.manager.assembler.PolicyDTOAssembler;
import org.simbasecurity.core.service.manager.assembler.UserDTOAssembler;
import org.simbasecurity.core.service.manager.dto.ConditionDTO;
import org.simbasecurity.core.service.manager.dto.PolicyDTO;
import org.simbasecurity.core.service.manager.dto.UserDTO;
import org.simbasecurity.core.spring.quartz.ExtendedCronExpression;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static org.simbasecurity.core.service.manager.assembler.ConditionDTOAssembler.assemble;

@Transactional
@Controller
@RequestMapping("condition")
public class ConditionManagerService {

    @Autowired
    private ConditionRepository conditionRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserRepository userRepository;



    @RequestMapping("findAll")
    @ResponseBody
    public Collection<ConditionDTO> findAll() {
        return assemble(conditionRepository.findAll());
    }

    @RequestMapping("findPolicies")
    @ResponseBody
    public Collection<PolicyDTO> findPolicies(@RequestBody ConditionDTO condition) {
        final Condition attachedCondition = conditionRepository.refreshWithOptimisticLocking(condition);
        return PolicyDTOAssembler.assemble(conditionRepository.findPolicies(attachedCondition));
    }

    @RequestMapping("findUsers")
    @ResponseBody
    public Collection<UserDTO> findUsers(@RequestBody ConditionDTO condition) {
        final Condition attachedCondition = conditionRepository.refreshWithOptimisticLocking(condition);
        return UserDTOAssembler.assemble(attachedCondition.getExemptedUsers());
    }

    @RequestMapping("refresh")
    @ResponseBody
    public ConditionDTO refresh(@RequestBody ConditionDTO condition) {
        return assemble(conditionRepository.lookUp(condition));
    }

    @RequestMapping("addOrUpdate")
    @ResponseBody
    public ConditionDTO addOrUpdate(@RequestBody ConditionDTO condition, @RequestBody Collection<UserDTO> users, @RequestBody Collection<PolicyDTO> policies) {
        final Condition attachedCondition = findOrCreate(condition);

        final Collection<User> attachedUsers = userRepository.refreshWithOptimisticLocking(users);
        attachedCondition.setExemptedUsers(new HashSet<User>(attachedUsers));

        final Collection<Policy> attachedPolicies = policyRepository.refreshWithOptimisticLocking(policies);
        conditionRepository.updatePolicies(attachedCondition, attachedPolicies);

        conditionRepository.flush();
        return assemble(attachedCondition);
    }

    private Condition findOrCreate(@RequestBody ConditionDTO condition) {
        final Condition attachedCondition;

        if (condition.getId() == 0L) {
            attachedCondition = conditionRepository.persist(assemble(condition));
        } else {
            attachedCondition = conditionRepository.refreshWithOptimisticLocking(condition);
            BeanUtils.copyProperties(condition, attachedCondition);
        }

        return attachedCondition;
    }

    @RequestMapping("remove")
    @ResponseBody
    public void remove(@RequestBody ConditionDTO condition) {
        final Condition conditionEntity = conditionRepository.refreshWithOptimisticLocking(condition);

        conditionRepository.updatePolicies(conditionEntity, Collections.<Policy>emptySet());
        conditionRepository.remove(conditionEntity);
    }

    @RequestMapping("validateTimeCondition")
    @ResponseBody
    public boolean validateTimeCondition(@RequestBody String startCondition, @RequestBody String endCondition) {
        if (!isExpressionValid(startCondition)) {
            throw new SimbaException(SimbaMessageKey.INVALID_START_CONDITION);
        }

        if (!isExpressionValid(endCondition)) {
            throw new SimbaException(SimbaMessageKey.INVALID_END_CONDITION);
        }

        return true;

    }

    private static boolean isExpressionValid(final String expression) {
        try {
            new ExtendedCronExpression(expression);
            return true;
        } catch (ParseException ignored) {
            return false;
        }
    }
}
