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
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class GroupDatabaseRepositoryTest extends PersistenceTestCase {

	private static final String DUMMY_GROUP_NAME = "dummy";
    private static final String DUMMY_GROUP_CN = "CN";
	private GroupEntity group;
    private GroupEntity group2;

    @Autowired
    private GroupDatabaseRepository groupDatabaseRepository;

    @Before
	public void setUp() {
		group = new GroupEntity(DUMMY_GROUP_NAME, DUMMY_GROUP_CN);
        group2 = new GroupEntity(DUMMY_GROUP_NAME, "otherCN");
		persistAndRefresh(group, group2);
	}

	@Test
	public void findByName_success() {
		 Group result = groupDatabaseRepository.findByCN(DUMMY_GROUP_CN);
		 assertNotNull(result);
		 assertEquals(DUMMY_GROUP_NAME, result.getName());
	}

    @Test
    public void find() {
        User user = UserTestBuilder.aDefaultUser().build();
        user.addGroup(group);
        persistAndRefresh(user);

        Collection<Group> result = groupDatabaseRepository.find(user);

        assertThat(result).containsOnly(group);
    }

}
