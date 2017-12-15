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
import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.service.config.CoreConfigurationService;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Duration;
import java.util.UUID;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.MAX_LOGIN_ELAPSED_TIME;

@Entity
@Table(name = "SIMBA_SSO_TOKEN_MAPPING")
public class SSOTokenMappingEntity implements SSOTokenMapping {

    @Id
    private String token;

    private String ssoToken;
    private long creationTime;

    public SSOTokenMappingEntity() {
    }

    public SSOTokenMappingEntity(SSOToken ssoToken) {
        this.token = UUID.randomUUID().toString();
        this.ssoToken = ssoToken.getToken();
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public SSOToken getSSOToken() {
        return new SSOToken(ssoToken);
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean isExpired() {
        return creationTime + getMaxLoginElapsedTime() < System.currentTimeMillis();
    }

    private long getMaxLoginElapsedTime() {
        Integer maxElapsedTime = getConfigurationService().getValue(MAX_LOGIN_ELAPSED_TIME);
        return Duration.of(maxElapsedTime, MAX_LOGIN_ELAPSED_TIME.getChronoUnit()).toMillis();
    }

    private CoreConfigurationService getConfigurationService() {
        return GlobalContext.locate(CoreConfigurationService.class);
    }
}
