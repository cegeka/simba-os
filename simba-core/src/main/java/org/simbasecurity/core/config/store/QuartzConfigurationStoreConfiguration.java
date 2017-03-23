package org.simbasecurity.core.config.store;

import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class QuartzConfigurationStoreConfiguration {

    @Bean(name = "configurableJobNames")
    public Map<ConfigurationParameter, String> configurableJobNames() {
        Map<ConfigurationParameter, String> map = new HashMap<>();
        map.put(SimbaConfigurationParameter.PURGE_SESSION_INTERVAL, "Purge Expired Sessions");
        map.put(SimbaConfigurationParameter.MARK_PASSWORD_CHANGE_EXPRESSION, "Mark Users For Password Change");
        return map;
    }

}
