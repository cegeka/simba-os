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

public final class ConfigurationHelper {

    private final String name;
    private final boolean unique;
    private final Type<?> typeConverter;
    private final StoreType storeType;
    private final String defaultValue;

    public ConfigurationHelper(String name, StoreType storeType, boolean unique, Class<? extends Type<?>> type, String defaultValue) {
        this.name = name;
        this.unique = unique;
        try {
            this.typeConverter = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        this.storeType = storeType;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public boolean isUnique() {
        return unique;
    }

    public Type<?> getTypeConverter() {
        return typeConverter;
    }

    public StoreType getStoreType() {
        return storeType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
