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

import org.junit.Test;
import org.simbasecurity.core.domain.ExcludedResource;
import org.simbasecurity.core.domain.ExcludedResourceEntity;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ExcludedResourceDatabaseRepositoryTest extends PersistenceTestCase {

    private static final String EXCLUSION_PATTERN = "*/excluded/*";
    protected CoreConfigurationService configurationServiceMock;

    @Autowired
    private ExcludedResourceDatabaseRepository excludedResourceDatabaseRepository;

    @Test
    public void isResourceExcluded_resourceExcluded() throws Exception {
        ExcludedResource excludedResource = new ExcludedResourceEntity(EXCLUSION_PATTERN, true);

        persistAndRefresh(excludedResource);

        assertTrue(excludedResourceDatabaseRepository.isResourceExcluded("/excluded/resource.html"));
    }

    @Test
    public void isResourceExcluded_resourceNotExcluded() throws Exception {
        ExcludedResource excludedResource = new ExcludedResourceEntity(EXCLUSION_PATTERN, true);

        persistAndRefresh(excludedResource);

        assertFalse(excludedResourceDatabaseRepository.isResourceExcluded("/notexcluded/resource.html"));
    }

    @Test
    public void isResourceLoggingExcluded_loggingExcluded() {
        ExcludedResource excludedResource = new ExcludedResourceEntity(EXCLUSION_PATTERN, true);

        persistAndRefresh(excludedResource);

        assertTrue(excludedResourceDatabaseRepository.isResourceLoggingExcluded("/excluded/resource.html"));
    }

    @Test
    public void isResourceLoggingExcluded_loggingNotExcluded() {
        ExcludedResource excludedResource = new ExcludedResourceEntity(EXCLUSION_PATTERN, false);

        persistAndRefresh(excludedResource);

        assertFalse(excludedResourceDatabaseRepository.isResourceLoggingExcluded("/excluded/resource.html"));
    }

}
