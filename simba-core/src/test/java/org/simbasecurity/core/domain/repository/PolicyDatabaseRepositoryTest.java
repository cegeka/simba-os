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
package org.simbasecurity.core.domain.repository;

import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.core.domain.*;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class PolicyDatabaseRepositoryTest extends PersistenceTestCase {

    private UserEntity user;
    private PolicyEntity policy1;
    private PolicyEntity policy2;
    private PolicyEntity policy3;
    private PolicyEntity policy4;
    private PolicyEntity policy5;
    private RuleEntity rule1;

    @Autowired
    private PolicyDatabaseRepository policyDatabaseRepository;
    private RoleEntity role1;
    private RoleEntity role2;
    private RoleEntity role3;

    @Before
    public void setUp() {
        user = new UserEntity("jos");
        rule1 = new ResourceRuleEntity("aansluitingen");
        role1 = new RoleEntity("venn_dossierbeheerder");
        role2 = new RoleEntity("ss_dossierbeheerder");
        role3 = new RoleEntity("kb_dossierbeheerder");
        policy1 = new PolicyEntity("vennootschappenReadPolicy");
        policy2 = new PolicyEntity("ssReadPolicy");
        policy3 = new PolicyEntity("kbReadPolicy");
        policy4 = new PolicyEntity("eloketReadPolicy");
        policy5 = new PolicyEntity("omReadPolicy");
        persistAndRefresh(user, role1, role2, role3, policy1, policy2, policy3, policy4, policy5, rule1);

        role1.addPolicy(policy1);
        role1.addPolicy(policy2);
        role2.addPolicy(policy3);
        role3.addPolicy(policy4);
        user.addRole(role1);
        user.addRole(role2);
        policy1.addRule(rule1);
    }

    @Test
    public void find() {
        Collection<Policy> collection = policyDatabaseRepository.find(user);
        assertEquals(3, collection.size());
        assertTrue(collection.containsAll(Arrays.asList(policy1, policy2, policy3)));
        assertFalse(collection.contains(policy4));
        assertFalse(collection.contains(policy5));
    }

    @Test
    public void canFindPolicyForRule() throws Exception {
        Policy policy = policyDatabaseRepository.find(rule1);
        assertEquals(policy1, policy);
    }

    @Test
    public void canFindPoliciesById() throws Exception {
        Collection<Policy> result = policyDatabaseRepository.findAllByIds(Arrays.asList(policy1.getId(), policy3.getId()));
        assertThat(result).containsOnly(policy1, policy3);
    }

    @Test
    public void findPoliciesNotLinked() throws Exception {
        Collection<Policy> result = policyDatabaseRepository.findNotLinked(role1);
        assertThat(result).containsOnly(policy3, policy4, policy5);
    }

}
