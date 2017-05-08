/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.simbasecurity.client.authorization.caching;

import org.apache.thrift.TException;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.api.service.thrift.PolicyDecision;
import org.simbasecurity.client.util.PolicyDecisionHelper;

import java.util.Map;
import java.util.function.Supplier;

public class AuthorizationCachingServiceImpl implements AuthorizationService.Iface {

    private Supplier<AuthorizationService.Iface> authorizationServiceSupplier;

    private Map<AuthorizationKey, PolicyDecision> resourceRuleCache = new SoftHashMap<>();
    private Map<AuthorizationKey, PolicyDecision> urlRuleCache = new SoftHashMap<>();

    public AuthorizationCachingServiceImpl(Supplier<AuthorizationService.Iface> authorizationServiceSupplier) {
        this.authorizationServiceSupplier = authorizationServiceSupplier;
    }

    @Override
    public PolicyDecision isResourceRuleAllowed(String username, String resourcename, String operation)
    throws TException {
        AuthorizationKey authorizationKey = new AuthorizationKey(username, resourcename, operation);

        PolicyDecision policyDecision = resourceRuleCache.get(authorizationKey);

        if (policyDecision == null || PolicyDecisionHelper.isExpired(policyDecision)) {
            policyDecision = authorizationServiceSupplier.get().isResourceRuleAllowed(username, resourcename, operation);
            resourceRuleCache.put(authorizationKey, policyDecision);
        }

        return policyDecision;
    }

    @Override
    public PolicyDecision isURLRuleAllowed(String username, String resourcename, String method) throws TException {
        AuthorizationKey authorizationKey = new AuthorizationKey(username, resourcename, method);

        PolicyDecision policyDecision = urlRuleCache.get(authorizationKey);

        if (policyDecision == null || PolicyDecisionHelper.isExpired(policyDecision)) {
            policyDecision = authorizationServiceSupplier.get().isURLRuleAllowed(username, resourcename, method);
            urlRuleCache.put(authorizationKey, policyDecision);
        }

        return policyDecision;
    }

    public void invalidate() {
        urlRuleCache.clear();
        resourceRuleCache.clear();
    }

    public void invalidate(String userName) {
        removeData(userName, urlRuleCache);
        removeData(userName, resourceRuleCache);
    }

    @Override
    public PolicyDecision isUserInRole(String username, String roleName) throws TException {
        return authorizationServiceSupplier.get().isUserInRole(username, roleName);
    }

    private void removeData(String userName, Map<AuthorizationKey, PolicyDecision> map) {
        for (AuthorizationKey key : map.keySet()) {
            if (key.getUsername().equals(userName)) {
                map.remove(key);
            }
        }
    }
}
