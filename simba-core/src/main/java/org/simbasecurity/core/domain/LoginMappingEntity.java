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
import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Duration;
import java.util.UUID;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.MAX_LOGIN_ELAPSED_TIME;


@Entity
@Table(name = "SIMBA_LOGIN_MAPPING")
public class LoginMappingEntity implements LoginMapping {
    private static final Logger LOG = LoggerFactory.getLogger(LoginMappingEntity.class);

    static final int MAX_URL_LENGTH = 1024;

    @Id
    private String token;
    private String targetURL;
    private long creationTime;

    public LoginMappingEntity() {
    }

    private LoginMappingEntity(String targetURL) {
        this.targetURL = targetURL;

        this.token = UUID.randomUUID().toString();
        this.creationTime = System.currentTimeMillis();
    }

    public static LoginMappingEntity create(String targetURL) {
        LoginMappingEntity entity = new LoginMappingEntity(targetURL);
        entity.validate();
        return entity;
    }

    private void validate() {
        final String ERROR_PREFIX = "SIMBA-12899: Target URL is too large for LoginMappingEntity: {}";
        if (targetURL != null && targetURL.length() > MAX_URL_LENGTH) {
            LOG.error(ERROR_PREFIX, this);
            targetURL = targetURL.substring(0, MAX_URL_LENGTH);
        }
    }

    public String getToken() {
        return token;
    }

    public String getTargetURL() {
        return targetURL;
    }

    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("Token", token);
        builder.append("targetURL", targetURL);
        builder.append("TimeStamp", creationTime);
        return builder.toString();
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
