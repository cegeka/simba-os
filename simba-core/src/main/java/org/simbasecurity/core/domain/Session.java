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

/**
 * @since 1.0
 */
public interface Session {

    /**
     * @return the {@link User} for this session.
     */
    User getUser();

    /**
     * @return the IP Address of the client for which the session is created.
     */
    String getClientIpAddress();

    /**
     * @return the exact time at which the session was created.
     */
    long getCreationTime();

    /**
     * @return the exact time at which the session was last accessed.
     */
    long getLastAccessTime();

    /**
     * Update the last access time of the session to this exact moment.
     *
     * @see #getLastAccessTime()
     */
    void updateLastAccesTime();

    /**
     * @return the current SSO Token for the session.
     */
    SSOToken getSSOToken();

    /**
     * @return <tt>true</tt> if the session is expired; <tt>false</tt> otherwise
     */
    boolean isExpired();

    /**
     * @return the name of the host server for the session.
     */
    String getHostServerName();
}
