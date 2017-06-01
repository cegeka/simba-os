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
package org.simbasecurity.manager.service.rest.dto;


public final class SessionDTO {

    private String ssoToken;
    private String userName;
    private String clientIpAddress;
    private long creationTime;
    private long lastAccessTime;

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public void setClientIpAddress(final String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(final long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(final long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getSsoToken() {
        return ssoToken;
    }

    public void setSsoToken(String ssoToken) {
        this.ssoToken = ssoToken;
    }
}

