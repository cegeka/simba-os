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

package org.simbasecurity.core.domain;

import org.simbasecurity.api.service.thrift.SSOToken;

public interface SSOTokenMapping {
    /**
     * @return the generated token.
     */
    String getToken();

    /**
     * @return the mapped session token
     */
    SSOToken getSSOToken();

    /**
     * @return the creation time stamp for the mapping
     */
    long getCreationTime();

    /**
     * @return <tt>true</tt> if the mapping is expired; <tt>false</tt> otherwise
     */
    boolean isExpired();

}
