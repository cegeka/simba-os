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

package org.simbasecurity.core.service.config;

import org.simbasecurity.core.config.ConfigurationStore;
import org.simbasecurity.core.config.StoreType;
import org.simbasecurity.core.config.store.DatabaseConfigurationStore;
import org.simbasecurity.core.config.store.QuartzConfigurationStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;

@Configuration
public class ConfigurationStoreConfiguration {

    @Autowired private DatabaseConfigurationStore databaseConfigurationStore;
    @Autowired private QuartzConfigurationStore quartzConfigurationStore;

    @Bean(name = "storeTypes")
    public EnumMap<StoreType, ConfigurationStore> storeTypes() {
        EnumMap<StoreType, ConfigurationStore> map = new EnumMap<StoreType, ConfigurationStore>(StoreType.class);
        map.put(StoreType.DATABASE, databaseConfigurationStore);
        map.put(StoreType.QUARTZ, quartzConfigurationStore);
        return map;
    }
}
