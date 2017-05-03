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
package org.simbasecurity.core.service.manager.assembler;

import static org.simbasecurity.core.service.manager.assembler.VersionedDTOAssemblerUtil.*;

import java.util.ArrayList;
import java.util.Collection;

import org.simbasecurity.core.domain.AbstractVersionedEntity;
import org.simbasecurity.core.domain.ResourceRule;
import org.simbasecurity.core.domain.Rule;
import org.simbasecurity.core.domain.URLRule;
import org.simbasecurity.core.service.manager.dto.ResourceRuleDTO;
import org.simbasecurity.core.service.manager.dto.RuleDTO;
import org.simbasecurity.core.service.manager.dto.URLRuleDTO;

public class RuleDTOAssembler {
    private RuleDTOAssembler() {
    }

    public static Collection<RuleDTO> assemble(final Collection<Rule> rules) {
        final Collection<RuleDTO> ruleDTOs = new ArrayList<RuleDTO>(rules.size());
        for (final Rule rule : rules) {
            ruleDTOs.add(assemble(rule));
        }
        return ruleDTOs;
    }

    public static RuleDTO assemble(final ResourceRule rule) {
        final ResourceRuleDTO ruleDTO = new ResourceRuleDTO();
        applyVersionAndId((AbstractVersionedEntity) rule, ruleDTO);
        applyRuleDTOAssembling(rule, ruleDTO);
        ruleDTO.setCreateAllowed(rule.isCreateAllowed());
        ruleDTO.setDeleteAllowed(rule.isDeleteAllowed());
        ruleDTO.setReadAllowed(rule.isReadAllowed());
        ruleDTO.setWriteAllowed(rule.isWriteAllowed());

        return ruleDTO;
    }

    public static RuleDTO assemble(final URLRule rule) {
        final URLRuleDTO ruleDTO = new URLRuleDTO();
        applyVersionAndId((AbstractVersionedEntity) rule, ruleDTO);
        applyRuleDTOAssembling(rule, ruleDTO);
        ruleDTO.setGetAllowed(rule.isGetAllowed());
        ruleDTO.setPostAllowed(rule.isPostAllowed());

        return ruleDTO;
    }

    public static RuleDTO assemble(final Rule rule) {
        if (rule instanceof ResourceRule) {
            return assemble((ResourceRule) rule);
        } else if (rule instanceof URLRule) {
            return assemble((URLRule) rule);
        }
        throw new IllegalArgumentException("Unknown type " + rule.getClass());
    }

    private static void applyRuleDTOAssembling(final Rule rule, final RuleDTO ruleDTO) {
        ruleDTO.setName(rule.getName());
        ruleDTO.setResourceName(rule.getResourceName());
    }
}
