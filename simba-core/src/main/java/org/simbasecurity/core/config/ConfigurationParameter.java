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
