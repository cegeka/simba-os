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
package org.simbasecurity.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;


@Transactional(transactionManager = "transactionManager")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:persistenceTestContext.xml")
@Rollback
public abstract class DatabaseTestCase extends LocatorTestCase {

    protected CoreConfigurationService configurationServiceMock;

    @Before
    public void setUpCommonLocatables() {
        implantMock(UserValidator.class);
        implantMock(PasswordValidator.class);

        configurationServiceMock = implantMock(CoreConfigurationService.class);
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.DEFAULT_PASSWORD)).thenReturn("aPassword");
    }
}
