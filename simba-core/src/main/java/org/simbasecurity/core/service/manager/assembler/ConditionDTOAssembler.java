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
import org.simbasecurity.core.domain.Condition;
import org.simbasecurity.core.domain.condition.TimeCondition;
import org.simbasecurity.core.service.manager.dto.ConditionDTO;
import org.simbasecurity.core.service.manager.dto.TimeConditionDTO;

public final class ConditionDTOAssembler {

    private ConditionDTOAssembler() {
    }

    public static Collection<ConditionDTO> assemble(final Collection<Condition> conditions) {
        final Collection<ConditionDTO> conditionDTOs = new ArrayList<ConditionDTO>(conditions.size());
        for (final Condition condition : conditions) {
            conditionDTOs.add(assemble(condition));
        }
        return conditionDTOs;
    }

    public static ConditionDTO assemble(final Condition condition) {
        if (condition instanceof TimeCondition) {
            return assemble((TimeCondition) condition);
        }
        throw new IllegalArgumentException("Unknown type " + condition.getClass());
    }

    public static Condition assemble(final ConditionDTO condition) {
        if (condition instanceof TimeConditionDTO) {
            return assemble((TimeConditionDTO) condition);
        }
        throw new IllegalArgumentException("Unknown type " + condition.getClass());
    }

    private static TimeConditionDTO assemble(final TimeCondition condition) {
        final TimeConditionDTO conditionDTO = new TimeConditionDTO();
        applyVersionAndId((AbstractVersionedEntity) condition, conditionDTO);
        conditionDTO.setName(condition.getName());
        conditionDTO.setStartCondition(condition.getStartCondition());
        conditionDTO.setEndCondition(condition.getEndCondition());
        return conditionDTO;
    }

    private static Condition assemble(final TimeConditionDTO condition) {
        final Condition timeCondition = new TimeCondition(condition.getStartCondition(), condition.getEndCondition());
        timeCondition.setName(condition.getName());
        return timeCondition;
    }

}
