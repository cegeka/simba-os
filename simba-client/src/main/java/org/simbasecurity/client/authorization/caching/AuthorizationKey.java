/*
 * Copyright 2013 Simba Open Source
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
package org.simbasecurity.client.authorization.caching;

final class AuthorizationKey {

    private final String username;
    private final String resourcename;
    private final String operation;
    private final int hashCode;

    public AuthorizationKey(String username, String resourcename, String operation) {
        this.username = username;
        this.resourcename = resourcename;
        this.operation = operation;

        this.hashCode = computeHashCode();
    }

    public String getUsername() {
        return username;
    }

    private int computeHashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + operation.hashCode();
        result = prime * result + resourcename.hashCode();
        result = prime * result + username.hashCode();

        return result;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AuthorizationKey other = (AuthorizationKey) obj;

        return username.equals(other.username) && resourcename.equals(other.resourcename)
                && operation.equals(other.operation);
    }

    @Override
    public String toString() {
        return username + "-" + resourcename + "-" + operation;
    }

}
