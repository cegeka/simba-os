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
package org.simbasecurity.client.principal;

import java.security.Principal;

/**
 * Stores data of the authenticated user.
 *
 * @since 1.0
 */
public final class SimbaPrincipal implements Principal, Comparable<SimbaPrincipal> {

    public static final String SIMBA_USER_CTX_KEY = "simba.user";

    String username;

    /**
     * Create a SimbaPrincipal with an username.
     *
     * @param userName String
     */
    public SimbaPrincipal(final String userName) {
        this.username = userName;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public int compareTo(final SimbaPrincipal otherPrincipal) {
        return this.username.compareTo(otherPrincipal.username);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (this.username == null) {
            result = prime * result + 0;
        } else {
            result = prime * result + this.username.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimbaPrincipal other = (SimbaPrincipal) obj;
        if (this.username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!this.username.equals(other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SimbaPrincipal [username=" + this.username + "]";
    }
}
