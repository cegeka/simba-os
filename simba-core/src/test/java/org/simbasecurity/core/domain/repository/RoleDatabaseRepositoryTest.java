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
package org.simbasecurity.core.domain.repository;

import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.core.domain.*;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RoleDatabaseRepositoryTest extends PersistenceTestCase {

    private static final String SS_DOSSIERBEHEERDER = "ss_dossierbeheerder";
    private static final String KB_DOSSIERBEHEERDER = "kb_dossierbeheerder";
    private static final String VENN_DOSSIERBEHEERDER = "venn_dossierbeheerder";
    protected CoreConfigurationService configurationServiceMock;

    @Autowired
    private RoleDatabaseRepository roleDatabaseRepository;

    private RoleEntity role1;
    private User user;
    private PolicyEntity policy;

    @Before
    public void setUp() {
        user = UserTestBuilder.aDefaultUser().build();
        policy = new PolicyEntity("aPolicy");
        role1 = new RoleEntity(VENN_DOSSIERBEHEERDER);

        RoleEntity role2 = new RoleEntity(SS_DOSSIERBEHEERDER);
        RoleEntity role3 = new RoleEntity(KB_DOSSIERBEHEERDER);
        persistAndRefresh(user, role1, role2, role3, policy);

        role1.addUser(user);
        role1.addPolicy(policy);
    }

    @Test
    public void findByName() {
        Role result = roleDatabaseRepository.findByName(SS_DOSSIERBEHEERDER);
        assertNotNull(result);
        assertEquals(result.getName(), SS_DOSSIERBEHEERDER);
    }

    @Test
    public void findForPolicy() {
        Collection<Role> result = roleDatabaseRepository.findForPolicy(policy);
        assertThat(result).containsOnly(role1);
    }

    @Test
    public void findForUser() {
        Collection<Role> result = roleDatabaseRepository.findForUser(user);
        assertThat(result).containsOnly(role1);
    }

    @Test
    public void testRemoveRole() {
        GroupEntity group = new GroupEntity("testGroup", "OU");
        PolicyEntity policy = new PolicyEntity("policy");
        persistAndRefresh(group, policy);
        role1.addGroup(group);
        role1.addPolicy(policy);

        roleDatabaseRepository.remove(role1);

        assertThat(group.getRoles()).isEmpty();
        assertThat(user.getRoles()).isEmpty();
        assertThat(policy.getRoles()).isEmpty();
        assertThat(role1.getGroups()).isEmpty();
        assertThat(role1.getUsers()).isEmpty();
        assertThat(role1.getPolicies()).isEmpty();
    }

}
