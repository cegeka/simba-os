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
import org.simbasecurity.core.domain.SSOTokenMappingEntity;
import org.simbasecurity.core.domain.repository.SSOTokenMappingDatabaseRepository;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collection;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.MAX_LOGIN_ELAPSED_TIME;

@Service
@Transactional
public class SSOTokenMappingServiceImpl implements SSOTokenMappingService {

    @Autowired private SSOTokenMappingDatabaseRepository ssoTokenMappingDatabaseRepository;
    @Autowired private CoreConfigurationService coreConfigurationService;

    @Override
    public SSOTokenMapping createMapping(SSOToken token) {
        SSOTokenMappingEntity mapping = new SSOTokenMappingEntity(token);
        ssoTokenMappingDatabaseRepository.persist(mapping);
        return mapping;
    }

    @Override
    public SSOToken getSSOToken(String ssoTokenKey) {
        SSOTokenMapping tokenMapping = getSSOTokenMapping(ssoTokenKey);
        return tokenMapping == null ? null : tokenMapping.getSSOToken();
    }

    private SSOTokenMapping getSSOTokenMapping(String ssoTokenKey) {
        if (ssoTokenKey == null) return null;
        return ssoTokenMappingDatabaseRepository.findByToken(ssoTokenKey);
    }

    @Override
    public void destroyMapping(String ssoTokenKey) {
        if (ssoTokenKey != null) {
            ssoTokenMappingDatabaseRepository.remove(ssoTokenKey);
        }
    }

    @Override
    public void purgeExpiredMappings() {
        Collection<SSOTokenMapping> mappings = ssoTokenMappingDatabaseRepository.findAll();

        mappings.stream()
                .filter(this::isExpired)
                .forEach(mapping -> ssoTokenMappingDatabaseRepository.remove(mapping));
    }


    @Override
    public boolean isExpired(String ssoTokenKey) {
        return isExpired(getSSOTokenMapping(ssoTokenKey));
    }

    public boolean isExpired(SSOTokenMapping ssoTokenMapping) {
        return ssoTokenMapping != null && ssoTokenMapping.getCreationTime() + getMaxLoginElapsedTime() < System.currentTimeMillis();
    }

    private long getMaxLoginElapsedTime() {
        Long maxElapsedTime = coreConfigurationService.getValue(MAX_LOGIN_ELAPSED_TIME);
        return Duration.of(maxElapsedTime, MAX_LOGIN_ELAPSED_TIME.getChronoUnit()).toMillis();
    }

}
