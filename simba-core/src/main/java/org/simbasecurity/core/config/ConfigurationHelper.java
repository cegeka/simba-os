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
