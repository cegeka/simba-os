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
package org.simbasecurity.core.service.authorization;

import org.apache.commons.io.FilenameUtils;
import org.apache.thrift.TException;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.api.service.thrift.PolicyDecision;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.domain.*;
import org.simbasecurity.core.domain.repository.RuleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.service.AuthorizationRequestContext;
import org.simbasecurity.core.service.errors.SimbaExceptionHandlingCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.simbasecurity.core.audit.AuditMessages.*;

@Service("authorizationService")
@Transactional
public class AuthorizationServiceImpl implements AuthorizationService.Iface {

    public static final PolicyDecision NEVER_ALLOWED = new PolicyDecision(false, Long.MAX_VALUE);
    public static final PolicyDecision ALWAYS_ALLOWED = new PolicyDecision(true, Long.MAX_VALUE);

    @Autowired
    private RuleRepository ruleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Audit audit;
    @Autowired
    private AuditLogEventFactory eventFactory;
    @Autowired
    private SimbaExceptionHandlingCaller simbaExceptionHandlingCaller;

    @Override
    public PolicyDecision isResourceRuleAllowed(String username, String resourceName, String operation) throws TException {
        return simbaExceptionHandlingCaller.call(() -> {
            return isResourceRuleAllowed(username, resourceName, ResourceOperationType.resolve(operation));
        });
    }

    private PolicyDecision isResourceRuleAllowed(String username, String resourceName, ResourceOperationType operationType) {
        AuthorizationRequestContext context = new AuthorizationRequestContext(username);
        Collection<ResourceRule> resourceRules = ruleRepository.findResourceRules(username, resourceName);

        PolicyDecision decision = null;

        for (ResourceRule resourceRule : resourceRules) {
            boolean allowed = resourceRule.getPolicy().applies(context) && resourceRule.isAllowed(operationType);
            long newTimestamp = resourceRule.getPolicy().getExpirationTimestamp(context);

            decision = determineDecisionBasedOn(decision, allowed, newTimestamp);
        }

        if (decision == null) {
            decision = NEVER_ALLOWED;
        }
        logAuthorizationDecision(username, RESOURCE_LABEL + resourceName + LOG_DELIM + operationType.name() + LOG_DELIM + decision.toString());
        return decision;
    }

    @Override
    public PolicyDecision isURLRuleAllowed(String username, String url, String httpMethod) throws TException {
        return simbaExceptionHandlingCaller.call(() -> {
            return isURLRuleAllowed(username, url, URLOperationType.resolve(httpMethod));
        });
    }

    private PolicyDecision isURLRuleAllowed(String username, String url, URLOperationType operationType) {
        AuthorizationRequestContext context = new AuthorizationRequestContext(username);
        Collection<URLRule> rules = ruleRepository.findURLRules(username);

        PolicyDecision decision = null;

        for (URLRule rule : rules) {
            if (FilenameUtils.wildcardMatch(url, rule.getResourceName())) {
                boolean allowed = rule.getPolicy().applies(context) && rule.isAllowed(operationType);
                long newTimestamp = rule.getPolicy().getExpirationTimestamp(context);

                decision = determineDecisionBasedOn(decision, allowed, newTimestamp);
            }
        }

        if (decision == null) {
            decision = NEVER_ALLOWED;
        }
        logAuthorizationDecision(username, URL_RESOURCE_LABEL + url + LOG_DELIM + operationType.name() + LOG_DELIM + decision.toString());
        return decision;
    }

    @Override
    public PolicyDecision isUserInRole(String username, String roleName) throws TException {
        return simbaExceptionHandlingCaller.call(() -> {
            User user = userRepository.findByName(username);
            if (user.hasRole(roleName)) {
                logAuthorizationDecision(username, USER_ALLOWED_IN_ROLE_LABEL + roleName + LOG_DELIM + ALWAYS_ALLOWED);
                return ALWAYS_ALLOWED;
            }

            logAuthorizationDecision(username, USER_ALLOWED_IN_ROLE_LABEL + roleName + LOG_DELIM + NEVER_ALLOWED);
            return NEVER_ALLOWED;
        });
    }

    private PolicyDecision determineDecisionBasedOn(PolicyDecision decision, boolean allowed, long newTimestamp) {
        if (decision == null) {
            decision = new PolicyDecision(allowed, newTimestamp);
        } else {
            if (decision.isAllowed()) {
                if (allowed && newTimestamp > decision.getExpirationTimestamp()) {
                    decision = new PolicyDecision(true, newTimestamp);
                }
            } else {
                if (allowed || newTimestamp < decision.getExpirationTimestamp()) {
                    decision = new PolicyDecision(allowed, newTimestamp);
                }
            }
        }
        return decision;
    }

    private void logAuthorizationDecision(String username, String message) {
        audit.log(eventFactory.createEventForAuthorizationDecision(username, message));
    }
}
