package org.simbasecurity.core.audit;

public enum AuditLogLevel {
	OFF(Integer.MAX_VALUE), INFO(1000), TRACE(500), ALL(Integer.MIN_VALUE);

    private final int value;

    private AuditLogLevel(int value) {
        this.value = value;
    }

    public boolean isLoggable(AuditLogLevel level) {
        return value != OFF.value && level.value >= value;
    }
}
