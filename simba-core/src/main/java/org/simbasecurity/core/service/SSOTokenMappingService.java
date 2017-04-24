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

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.domain.SSOTokenMapping;

public interface SSOTokenMappingService {
    /**
     * Create a temporary token linked to an existing session SSO token so the filter
     * can use this temporary token to fetch the actual SSOToken for which to create a
     * HTTP Cookie.
     *
     * @param token the SSOToken for which to create a mapping
     * @return the unique short-lived token to use
     */
    SSOTokenMapping createMapping(SSOToken token);

    SSOToken getSSOToken(String ssoTokenKey);

    void destroyMapping(String ssoTokenKey);

    void purgeExpiredMappings();
}
