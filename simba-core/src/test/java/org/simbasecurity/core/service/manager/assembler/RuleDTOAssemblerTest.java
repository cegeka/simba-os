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
package org.simbasecurity.core.service.manager.assembler;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.simbasecurity.core.domain.ResourceRule;
import org.simbasecurity.core.domain.ResourceRuleEntity;
import org.simbasecurity.core.domain.Rule;
import org.simbasecurity.core.domain.URLRule;
import org.simbasecurity.core.domain.URLRuleEntity;
import org.simbasecurity.core.service.manager.dto.ResourceRuleDTO;
import org.simbasecurity.core.service.manager.dto.RuleDTO;
import org.simbasecurity.core.service.manager.dto.URLRuleDTO;

public class RuleDTOAssemblerTest {

    @Test
    public void testAssembleSingleRule_ResourceRule() {
        ResourceRule resourceRule = createResourceRule();

        ResourceRuleDTO ruleData = (ResourceRuleDTO) RuleDTOAssembler.assemble(resourceRule);

        assertNotNull(ruleData);
        assertEquals(resourceRule.getName(), ruleData.getName());
        assertEquals(resourceRule.getResourceName(), ruleData.getResourceName());
        assertEquals(true, ruleData.isCreateAllowed());
        assertEquals(true, ruleData.isDeleteAllowed());
        assertEquals(true, ruleData.isReadAllowed());
        assertEquals(true, ruleData.isWriteAllowed());

        assertEquals(0, ruleData.getId());
        assertEquals(0, ruleData.getVersion());
    }

    private ResourceRule createResourceRule() {
        ResourceRuleEntity rule = new ResourceRuleEntity("resource rule name");
        rule.setResourceName("resource name");

        rule.setCreateAllowed(true);
        rule.setDeleteAllowed(true);
        rule.setReadAllowed(true);
        rule.setWriteAllowed(true);

        return rule;
    }

    @Test
    public void testAssembleSingleRule_UrlRule() {
        URLRule urlRule = createUrlRule();

        URLRuleDTO ruleData = (URLRuleDTO) RuleDTOAssembler.assemble(urlRule);

        assertNotNull(ruleData);
        assertEquals(urlRule.getName(), ruleData.getName());
        assertEquals(urlRule.getResourceName(), ruleData.getResourceName());
        assertEquals(true, ruleData.isGetAllowed());
        assertEquals(true, ruleData.isPostAllowed());

        assertEquals(0, ruleData.getId());
        assertEquals(0, ruleData.getVersion());
    }

    private URLRule createUrlRule() {
        URLRuleEntity rule = new URLRuleEntity("url rule name");
        rule.setResourceName("resource name");

        rule.setGetAllowed(true);
        rule.setPostAllowed(true);

        return rule;
    }

    @Test
    public void testAssembleMultipleRules() {
        Rule resourceRule = createResourceRule();
        Rule urlRule = createUrlRule();

        Collection<RuleDTO> ruleDataList = RuleDTOAssembler.assemble(Arrays.asList(resourceRule, urlRule));

        assertNotNull(ruleDataList);
        assertEquals(2, ruleDataList.size());

    }

}
