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
package org.simbasecurity.core.service;

import org.simbasecurity.core.domain.Session;
import org.springframework.stereotype.Service;

/**
 * The NoArchiveSessionServiceImpl doesn't do anything.  It's the default behavior, so no session information will be archived if the session is removed or expired.
 * You can use the ArchiveSessionToDatabaseTableServiceImpl in the SIMBA project or implement your own using the ArchiveSessionService interface.
 */
@Service("noSessionArchive")
public class NoArchiveSessionServiceImpl implements ArchiveSessionService {

	@Override
	public void archive(Session session) {
	}

}
