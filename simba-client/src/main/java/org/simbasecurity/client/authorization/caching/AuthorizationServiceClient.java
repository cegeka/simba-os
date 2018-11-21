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
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.api.service.thrift.PolicyDecision;
import org.simbasecurity.api.service.thrift.TSimbaError;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.client.util.PolicyDecisionHelper;

import java.util.List;
import java.util.Map;

/**
 * A client for the Simba authorization service. The client encapsulates a authorization cache for performance
 * reasons. The cache can be invalidated by calling the {@link #invalidate()} method to clear the entire cache,
 * or the {@link #invalidate(String)} method, to clear the cache for a specific user name.
 */
public class AuthorizationServiceClient implements AuthorizationService.Iface {

    private Map<AuthorizationKey, PolicyDecision> resourceRuleCache = new SoftHashMap<>();
    private Map<AuthorizationKey, PolicyDecision> urlRuleCache = new SoftHashMap<>();

    @Override
    public PolicyDecision isResourceRuleAllowed(String username, String resourcename, String operation) throws TException {
        AuthorizationKey authorizationKey = new AuthorizationKey(username, resourcename, operation);

        PolicyDecision policyDecision = resourceRuleCache.get(authorizationKey);

        if (policyDecision == null || PolicyDecisionHelper.isExpired(policyDecision)) {
            policyDecision = getAuthorizationServiceClient().isResourceRuleAllowed(username, resourcename, operation);
            resourceRuleCache.put(authorizationKey, policyDecision);
        }

        return policyDecision;
    }

    @Override
    public PolicyDecision isURLRuleAllowed(String username, String resourcename, String method) throws TException {
        AuthorizationKey authorizationKey = new AuthorizationKey(username, resourcename, method);

        PolicyDecision policyDecision = urlRuleCache.get(authorizationKey);

        if (policyDecision == null || PolicyDecisionHelper.isExpired(policyDecision)) {
            policyDecision = getAuthorizationServiceClient().isURLRuleAllowed(username, resourcename, method);
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
        return getAuthorizationServiceClient().isUserInRole(username, roleName);
    }

    @Override
    public List<String> getRoles(String username) throws TException {
        return getAuthorizationServiceClient().getRoles(username);
    }

    private void removeData(String userName, Map<AuthorizationKey, PolicyDecision> map) {
        for (AuthorizationKey key : map.keySet()) {
            if (key.getUsername().equals(userName)) {
                map.remove(key);
            }
        }
    }

    protected AuthorizationService.Iface getAuthorizationServiceClient() throws TTransportException {
        THttpClient tHttpClient = new THttpClient(SimbaConfiguration.getSimbaAuthorizationURL());
        TProtocol tProtocol = new TJSONProtocol(tHttpClient);
        return new AuthorizationService.Client(tProtocol);
    }
}
