package org.simbasecurity.core.config.store;

import java.util.EnumMap;

import org.simbasecurity.core.config.ConfigurationParameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfigurationStoreConfiguration {

    @Bean(name = "configurableJobNames")
    public EnumMap<ConfigurationParameter, String> configurableJobNames() {
        EnumMap<ConfigurationParameter, String> map = new EnumMap<ConfigurationParameter, String>(ConfigurationParameter.class);
        map.put(ConfigurationParameter.PURGE_SESSION_INTERVAL, "Purge Expired Sessions");
        map.put(ConfigurationParameter.MARK_PASSWORD_CHANGE_EXPRESSION, "Mark Users For Password Change");
        return map;
    }

}
