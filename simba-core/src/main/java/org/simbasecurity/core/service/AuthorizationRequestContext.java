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
package org.simbasecurity.core.service;

/**
 * Contains data related to this authorization request, such as username. Used
 * by conditions to check if they apply.
 *
 * @see org.simbasecurity.core.domain.Condition
 */
public class AuthorizationRequestContext {

    private final String username;
    private final long time;

    /**
     * Initialize fields username and time. Time is set to current system time.
     */
    public AuthorizationRequestContext(String username) {
        this.username = username;
        this.time = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public long getTime() {
        return time;
    }

}
