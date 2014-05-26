/*
 * Copyright 2013 Simba Open Source
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
package org.simbasecurity.core.config.store;

import java.util.List;

import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationStore;

public class QuartzDummyConfigurationStore implements ConfigurationStore {

    @Override
    public String getValue(ConfigurationParameter parameter) {
        return null;
    }

    @Override
    public List<String> getValueList(ConfigurationParameter parameter) {
        return null;
    }

    @Override
    public String setValue(ConfigurationParameter parameter, String value) {
        return null;
    }

    @Override
    public List<String> setValueList(ConfigurationParameter parameter, List<String> valueList) {
        return null;
    }

}
