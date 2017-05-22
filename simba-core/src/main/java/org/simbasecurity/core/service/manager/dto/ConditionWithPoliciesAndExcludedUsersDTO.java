package org.simbasecurity.core.service.manager.dto;

import java.util.Collection;

public class ConditionWithPoliciesAndExcludedUsersDTO {
    private ConditionDTO condition;
    private Collection<UserDTO> excludedUsers;
    private Collection<PolicyDTO> policies;

    public ConditionDTO getCondition() {
        return condition;
    }

    public void setCondition(ConditionDTO condition) {
        this.condition = condition;
    }

    public Collection<PolicyDTO> getPolicies() {
        return policies;
    }

    public void setPolicies(Collection<PolicyDTO> policies) {
        this.policies = policies;
    }

    public Collection<UserDTO> getExcludedUsers() {
        return excludedUsers;
    }

    public void setExcludedUsers(Collection<UserDTO> excludedUsers) {
        this.excludedUsers = excludedUsers;
    }
}
