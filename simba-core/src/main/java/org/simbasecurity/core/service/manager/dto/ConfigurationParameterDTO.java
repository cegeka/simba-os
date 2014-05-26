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
package org.simbasecurity.core.service.manager.dto;

import org.simbasecurity.core.config.ConfigurationParameter;


public final class ConfigurationParameterDTO {

    private ConfigurationParameter name;
    private Object value;

    public ConfigurationParameterDTO() {
    }

    public ConfigurationParameterDTO(final ConfigurationParameter name, final Object value) {
        setName(name);
        setValue(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }

    public <T> void setValue(final T value) {
        this.value = value;
    }

    public void setName(final ConfigurationParameter name) {
        this.name = name;
    }

    public ConfigurationParameter getName() {
        return name;
    }
}

