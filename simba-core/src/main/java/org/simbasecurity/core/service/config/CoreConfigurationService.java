package org.simbasecurity.core.service.config;

import org.simbasecurity.api.service.thrift.ConfigurationService;
import org.simbasecurity.core.config.ConfigurationParameter;

public interface CoreConfigurationService extends ConfigurationService.Iface {
    @SuppressWarnings("unchecked")
    <T> T getValue(ConfigurationParameter parameter);

    <T> void changeParameter(ConfigurationParameter parameter, T value);
}
