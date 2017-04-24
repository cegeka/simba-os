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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.simbasecurity.core.domain.Condition;
import org.simbasecurity.core.domain.ConditionEntity;
import org.simbasecurity.core.domain.condition.TimeCondition;
import org.simbasecurity.core.service.AuthorizationRequestContext;
import org.simbasecurity.core.service.manager.dto.ConditionDTO;
import org.simbasecurity.core.service.manager.dto.TimeConditionDTO;

public class ConditionDTOAssemblerTest {

	@Test(expected = IllegalArgumentException.class)
	public void testAssembleSingleCondition_Failure() {
		@SuppressWarnings("serial")
		Condition condition = new ConditionEntity() {
			@Override
			protected boolean conditionApplies(AuthorizationRequestContext context) {
				return false;
			}
		};

		ConditionDTOAssembler.assemble(condition);
	}

	@Test
	public void testAssembleSingleCondition() {
		TimeConditionDTO condition = createTimeConditionData();

		TimeCondition conditionData = (TimeCondition) ConditionDTOAssembler.assemble(condition);

		assertNotNull(conditionData);
		assertEquals(0, conditionData.getId());
		assertEquals(condition.getName(), conditionData.getName());
		assertEquals(condition.getStartCondition(), conditionData.getStartCondition());
		assertEquals(condition.getEndCondition(), conditionData.getEndCondition());

		assertEquals(0, conditionData.getId());
		assertEquals(0, conditionData.getVersion());
	}

	@Test
	public void testAssembleSingleConditionData() {
		TimeCondition condition = createTimeCondition();

		TimeConditionDTO conditionData = (TimeConditionDTO) ConditionDTOAssembler.assemble(condition);

		assertNotNull(conditionData);
		assertEquals(0, conditionData.getId());
		assertEquals(condition.getName(), conditionData.getName());
		assertEquals(condition.getStartCondition(), conditionData.getStartCondition());
		assertEquals(condition.getEndCondition(), conditionData.getEndCondition());

		assertEquals(0, conditionData.getId());
		assertEquals(0, conditionData.getVersion());
	}

	private TimeConditionDTO createTimeConditionData() {
		TimeConditionDTO condition = new TimeConditionDTO();
		condition.setName("time condition");
		condition.setStartCondition("0 0 12 * * ?");
		condition.setEndCondition("0 0 12 * * ?");
		return condition;
	}

	private TimeCondition createTimeCondition() {
		TimeCondition condition = new TimeCondition();
		condition.setName("time condition");
		condition.setStartCondition("0 0 12 * * ?");
		condition.setEndCondition("0 0 12 * * ?");
		return condition;
	}

	@Test
	public void testAssembleMultipleConditionsData() {
		Condition condition = createTimeCondition();

		Collection<ConditionDTO> policyDataList = ConditionDTOAssembler.assemble(Arrays.asList(condition));

		assertNotNull(policyDataList);
		assertEquals(1, policyDataList.size());

	}

}
