package org.simbasecurity.core.service.config;

import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class DefaultConfigurationParameterClassesConfiguration {

    @Bean("defaultConfigurationParameterClasses")
    @Qualifier("defaultConfigurationParameterClasses")
    public List<Class<? extends ConfigurationParameter>> configurationParametersClasses() {
        return Collections.singletonList(SimbaConfigurationParameter.class);
    }
}
