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
package org.simbasecurity.manager.service.rest.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("condition.type.timecondition")
public final class TimeConditionDTO extends ConditionDTO {
    private String startCondition;
    private String endCondition;

    public String getStartCondition() {
        return startCondition;
    }

    public void setStartCondition(final String startCondition) {
        this.startCondition = startCondition;
    }

    public String getEndCondition() {
        return endCondition;
    }

    public void setEndCondition(final String endCondition) {
        this.endCondition = endCondition;
    }
}