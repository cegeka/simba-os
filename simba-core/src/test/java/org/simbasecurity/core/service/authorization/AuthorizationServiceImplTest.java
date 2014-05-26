/*
 * Copyright 2013 Simba Open Source
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
 */
package org.simbasecurity.core.service.authorization;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.PolicyDecision;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.domain.Policy;
import org.simbasecurity.core.domain.ResourceOperationType;
import org.simbasecurity.core.domain.ResourceRule;
import org.simbasecurity.core.domain.Role;
import org.simbasecurity.core.domain.RoleEntity;
import org.simbasecurity.core.domain.URLOperationType;
import org.simbasecurity.core.domain.URLRule;
import org.simbasecurity.core.domain.UserEntity;
import org.simbasecurity.core.domain.repository.RuleRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.service.AuthorizationRequestContext;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServiceImplTest {

    private static final long EXPIRATION_TIMESTAMP_1 = 100000L;
    private static final long EXPIRATION_TIMESTAMP_2 = 200000L;

    private static final String RESOURCE_OPERATION = "read";
    private static final String RESOURCE_NAME = "resourcename";

    private static final String URL_OPERATION = "get";
    private static final String URL = "http://localhost/test/jsp/testpage.faces";

    private static final String USERNAME = "username";

    @Mock private RuleRepository mockRuleRepository;
    @Mock private UserRepository mockUserRepository;
    @Mock private Audit auditMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @Mock private ResourceRule mockResourceRule;
    @Mock private ResourceRule mock2ndResourceRule;
    @Mock private URLRule mockURLRule;
    @Mock private Policy mockPolicy;
    @Mock private Policy mock2ndPolicy;

    @InjectMocks
    private AuthorizationServiceImpl authorizationServiceImpl;

    @Before
    public void setUp() {
        when(mockResourceRule.getPolicy()).thenReturn(mockPolicy);
        when(mockPolicy.getExpirationTimestamp(any(AuthorizationRequestContext.class))).thenReturn(
                EXPIRATION_TIMESTAMP_1);
        when(mock2ndResourceRule.getPolicy()).thenReturn(mock2ndPolicy);
        when(mock2ndPolicy.getExpirationTimestamp(any(AuthorizationRequestContext.class))).thenReturn(
                EXPIRATION_TIMESTAMP_2);

        when(mockURLRule.getPolicy()).thenReturn(mockPolicy);
    }

    @Test
    public void isURLRuleAllowed_noUrlRuleFound() {
        when(mockRuleRepository.findURLRules(USERNAME)).thenReturn(Collections.<URLRule>emptyList());

        PolicyDecision decision = authorizationServiceImpl.isURLRuleAllowed(USERNAME, URL, URL_OPERATION);

        assertFalse(decision.isAllowed());
        assertEquals(Long.MAX_VALUE, decision.getExpirationTimestamp());
    }

    @Test
    public void isURLRuleAllowed_urlRuleFoundAndResourcenameMatches() {
        when(mockURLRule.getResourceName()).thenReturn("*/test/*");
        when(mockURLRule.isAllowed(URLOperationType.resolve(URL_OPERATION))).thenReturn(true);
        when(mockPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(true);
        when(mockRuleRepository.findURLRules(USERNAME)).thenReturn(Collections.singletonList(mockURLRule));

        PolicyDecision decision = authorizationServiceImpl.isURLRuleAllowed(USERNAME, URL, URL_OPERATION);

        assertTrue(decision.isAllowed());
        assertEquals(EXPIRATION_TIMESTAMP_1, decision.getExpirationTimestamp());
    }

    @Test
    public void isURLRuleAllowed_urlRuleFoundAndResourcenameDoesNotMatch() {
        when(mockURLRule.getResourceName()).thenReturn("*/notmatching/*");

        PolicyDecision decision = authorizationServiceImpl.isURLRuleAllowed(USERNAME, URL, URL_OPERATION);

        assertFalse(decision.isAllowed());
        assertEquals(Long.MAX_VALUE, decision.getExpirationTimestamp());
    }

    @Test
    public void policyDecisionNeverWhenNoRule() {
        when(mockRuleRepository.findResourceRules(USERNAME, RESOURCE_NAME)).thenReturn(
                Collections.<ResourceRule>emptySet());

        PolicyDecision decision = authorizationServiceImpl.isResourceRuleAllowed(USERNAME, RESOURCE_NAME,
                RESOURCE_OPERATION);

        assertFalse(decision.isAllowed());

        assertEquals(Long.MAX_VALUE, decision.getExpirationTimestamp());

        verify(mockRuleRepository).findResourceRules(USERNAME, RESOURCE_NAME);
    }

    @Test
    public void policyAppliesAndRuleAllowedReturnsDecisionTrue() {
        when(mockRuleRepository.findResourceRules(USERNAME, RESOURCE_NAME)).thenReturn(
                Collections.singleton(mockResourceRule));
        when(mockPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(true);
        when(mockResourceRule.isAllowed(ResourceOperationType.resolve(RESOURCE_OPERATION))).thenReturn(true);

        PolicyDecision decision = authorizationServiceImpl.isResourceRuleAllowed(USERNAME, RESOURCE_NAME,
                RESOURCE_OPERATION);

        assertTrue(decision.isAllowed());
        assertEquals(EXPIRATION_TIMESTAMP_1, decision.getExpirationTimestamp());
    }

    @Test
    public void policyAppliesAndRuleDisallowedReturnsDecisionFalse() {
        when(mockRuleRepository.findResourceRules(USERNAME, RESOURCE_NAME)).thenReturn(
                Collections.singleton(mockResourceRule));
        when(mockPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(true);
        when(mockResourceRule.isAllowed(ResourceOperationType.resolve(RESOURCE_OPERATION))).thenReturn(false);

        PolicyDecision decision = authorizationServiceImpl.isResourceRuleAllowed(USERNAME, RESOURCE_NAME,
                RESOURCE_OPERATION);

        assertFalse(decision.isAllowed());
        assertEquals(EXPIRATION_TIMESTAMP_1, decision.getExpirationTimestamp());
    }

    @Test
    public void policyDoesNotApplyReturnsDecisionFalse() {
        when(mockRuleRepository.findResourceRules(USERNAME, RESOURCE_NAME)).thenReturn(
                Collections.singleton(mockResourceRule));
        when(mockPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(false);

        PolicyDecision decision = authorizationServiceImpl.isResourceRuleAllowed(USERNAME, RESOURCE_NAME,
                RESOURCE_OPERATION);

        assertFalse(decision.isAllowed());
        assertEquals(EXPIRATION_TIMESTAMP_1, decision.getExpirationTimestamp());
    }

    @Test
    public void policyAppliesAnd2ndRuleAllowedReturnsDecisionTrue_1stPolicyDoesNotApply() {
        when(mockRuleRepository.findResourceRules(USERNAME, RESOURCE_NAME)).thenReturn(
                Arrays.asList(mockResourceRule, mock2ndResourceRule));
        when(mockPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(false);
        when(mock2ndPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(true);
        when(mock2ndResourceRule.isAllowed(ResourceOperationType.resolve(RESOURCE_OPERATION))).thenReturn(true);

        PolicyDecision decision = authorizationServiceImpl.isResourceRuleAllowed(USERNAME, RESOURCE_NAME,
                RESOURCE_OPERATION);

        assertEquals(EXPIRATION_TIMESTAMP_2, decision.getExpirationTimestamp());
        assertTrue(decision.isAllowed());
    }

    @Test
    public void policyAppliesAnd2ndRuleDisallowedReturnsDecisionFalse_1stPolicyDoesNotApply() {
        when(mockRuleRepository.findResourceRules(USERNAME, RESOURCE_NAME)).thenReturn(
                Arrays.asList(mockResourceRule, mock2ndResourceRule));
        when(mockPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(false);

        when(mock2ndPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(true);
        when(mock2ndResourceRule.isAllowed(ResourceOperationType.resolve(RESOURCE_OPERATION))).thenReturn(false);

        PolicyDecision decision = authorizationServiceImpl.isResourceRuleAllowed(USERNAME, RESOURCE_NAME,
                RESOURCE_OPERATION);

        assertFalse(decision.isAllowed());
        assertEquals(EXPIRATION_TIMESTAMP_1, decision.getExpirationTimestamp());
    }

    @Test
    public void policyDoesNotApplyAnd2ndRuleAllowedReturnsDecisionFalseWithSmallestExpirationStamp_1stPolicyDoesNotApply() {
        when(mockRuleRepository.findResourceRules(USERNAME, RESOURCE_NAME)).thenReturn(
                Arrays.asList(mockResourceRule, mock2ndResourceRule));
        when(mockPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(false);
        when(mock2ndPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(false);

        PolicyDecision decision = authorizationServiceImpl.isResourceRuleAllowed(USERNAME, RESOURCE_NAME,
                RESOURCE_OPERATION);

        assertFalse(decision.isAllowed());
        assertEquals(EXPIRATION_TIMESTAMP_1, decision.getExpirationTimestamp());
    }

    @Test
    public void policyAppliesAnd2ndRuleAllowedReturnsDecisionTrue_1stRuleDisallowed() {
        when(mockRuleRepository.findResourceRules(USERNAME, RESOURCE_NAME)).thenReturn(
                Arrays.asList(mockResourceRule, mock2ndResourceRule));
        when(mockPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(true);
        when(mockResourceRule.isAllowed(ResourceOperationType.resolve(RESOURCE_OPERATION))).thenReturn(false);
        when(mock2ndPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(true);
        when(mock2ndResourceRule.isAllowed(ResourceOperationType.resolve(RESOURCE_OPERATION))).thenReturn(true);

        PolicyDecision decision = authorizationServiceImpl.isResourceRuleAllowed(USERNAME, RESOURCE_NAME,
                RESOURCE_OPERATION);

        assertTrue(decision.isAllowed());
        assertEquals(EXPIRATION_TIMESTAMP_2, decision.getExpirationTimestamp());
    }

    @Test
    public void policyAppliesAnd2ndRuleDisallowedReturnsDecisionFalseWithSmallestExpirationStamp_1stRuleDisallowed() {
        when(mockRuleRepository.findResourceRules(USERNAME, RESOURCE_NAME)).thenReturn(
                Arrays.asList(mockResourceRule, mock2ndResourceRule));
        when(mockPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(true);
        when(mockResourceRule.isAllowed(ResourceOperationType.resolve(RESOURCE_OPERATION))).thenReturn(false);
        when(mock2ndPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(true);
        when(mock2ndResourceRule.isAllowed(ResourceOperationType.resolve(RESOURCE_OPERATION))).thenReturn(false);

        PolicyDecision decision = authorizationServiceImpl.isResourceRuleAllowed(USERNAME, RESOURCE_NAME,
                RESOURCE_OPERATION);

        assertFalse(decision.isAllowed());
        assertEquals(EXPIRATION_TIMESTAMP_1, decision.getExpirationTimestamp());
    }

    @Test
    public void policyDoesNotApplyAnd2ndRuleAllowedReturnsDecisionFalse_1stRuleDisallowed() {
        when(mockRuleRepository.findResourceRules(USERNAME, RESOURCE_NAME)).thenReturn(
                Arrays.asList(mockResourceRule, mock2ndResourceRule));
        when(mockPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(true);
        when(mockResourceRule.isAllowed(ResourceOperationType.resolve(RESOURCE_OPERATION))).thenReturn(false);
        when(mock2ndPolicy.applies(any(AuthorizationRequestContext.class))).thenReturn(false);

        PolicyDecision decision = authorizationServiceImpl.isResourceRuleAllowed(USERNAME, RESOURCE_NAME,
                RESOURCE_OPERATION);

        assertFalse(decision.isAllowed());
        assertEquals(EXPIRATION_TIMESTAMP_1, decision.getExpirationTimestamp());
    }

    @Test
    public void isUserInRole_isInRole() {
        setUpIsUserInRole();

        assertTrue(authorizationServiceImpl.isUserInRole("testuser", "testrole").isAllowed());
    }

    @Test
    public void isUserInRole_isNotInRole() {
        setUpIsUserInRole();

        assertFalse(authorizationServiceImpl.isUserInRole("testuser", "testotherrole").isAllowed());
    }

    private void setUpIsUserInRole() {
        RoleEntity roleEntity = mock(RoleEntity.class);
        HashSet<Role> roles = new HashSet<Role>();
        roles.add(roleEntity);
        UserEntity userEntity = mock(UserEntity.class);

        when(roleEntity.getName()).thenReturn("testrole");
        when(userEntity.getRoles()).thenReturn(roles);
        when(mockUserRepository.findByName("testuser")).thenReturn(userEntity);
    }

}
