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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.simbasecurity.api.service.thrift.SSOToken;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SIMBA_SESSION")
public class SessionEntity implements Session {

    @Id
    private String ssoToken;

    private long creationTime;
    private long lastAccessTime;

    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    private User user;

    private String clientIpAddress;
    private String hostServerName;

    public SessionEntity() {
    }

    public SessionEntity(User user, SSOToken ssoToken, String clientIpAddress, String hostServerName) {
        this.creationTime = System.currentTimeMillis();
        this.lastAccessTime = creationTime;
        this.ssoToken = ssoToken.getToken();
        this.user = user;
        this.clientIpAddress = clientIpAddress;
        this.hostServerName = hostServerName;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public void updateLastAccesTime() {
        this.lastAccessTime = System.currentTimeMillis();
    }

    @Override
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public SSOToken getSSOToken() {
        return new SSOToken(ssoToken);
    }

    @Override
    public String getClientIpAddress() {
        return clientIpAddress;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getHostServerName() {
        return hostServerName;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("user", user);
        builder.append("SSO Token", ssoToken);
        builder.append("client IP", clientIpAddress);
        builder.append("host", hostServerName);
        builder.append("created", creationTime);
        builder.append("last accessed", lastAccessTime);
        return builder.toString();
    }

}
