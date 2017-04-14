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
