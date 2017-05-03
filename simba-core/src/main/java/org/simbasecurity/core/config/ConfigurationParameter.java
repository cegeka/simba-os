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

package org.simbasecurity.core.config;

import org.apache.commons.lang.StringUtils;

public interface ConfigurationParameter {

    default String getName() {
        return helper().getName();
    }

    default StoreType getStoreType() {
        return helper().getStoreType();
    }

    default boolean isUnique() {
        return helper().isUnique();
    }

    @SuppressWarnings("unchecked")
    default <T> T convertToType(String value) {
        if(StringUtils.isBlank(value)) {
            return (T) helper().getTypeConverter().convertToValue(helper().getDefaultValue());
        }
        return (T) helper().getTypeConverter().convertToValue(value);
    }

    default <T> String convertToString(T value) {
        return helper().getTypeConverter().convertToString(value);
    }

    ConfigurationHelper helper();
}
