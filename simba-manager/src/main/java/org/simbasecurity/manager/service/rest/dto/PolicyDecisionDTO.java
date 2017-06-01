package org.simbasecurity.manager.service.rest.dto;

import org.simbasecurity.api.service.thrift.PolicyDecision;

public class PolicyDecisionDTO {

    private boolean allowed;
    private long expirationTimestamp;

    public PolicyDecisionDTO(PolicyDecision decision) {
        this.allowed = decision.isAllowed();
        this.expirationTimestamp = decision.getExpirationTimestamp();
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public void setExpirationTimestamp(long expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }
}
