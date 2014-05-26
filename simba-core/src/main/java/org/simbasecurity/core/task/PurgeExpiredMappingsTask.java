/*
 * Copyright 2011 Simba Open Source
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
package org.simbasecurity.core.task;

import org.simbasecurity.core.service.LoginMappingService;
import org.simbasecurity.core.service.SSOTokenMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PurgeExpiredMappingsTask {
	private static final Logger LOG = LoggerFactory.getLogger(PurgeExpiredMappingsTask.class);

	@Autowired private LoginMappingService loginMappingService;
    @Autowired private SSOTokenMappingService ssoTokenMappingService;

	public void execute() {
		LOG.debug("Purging expired mappings");
		loginMappingService.purgeExpiredMappings();
        ssoTokenMappingService.purgeExpiredMappings();
	}
}
