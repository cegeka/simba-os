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

import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.domain.LoginMappingEntity;
import org.simbasecurity.core.domain.repository.LoginMappingRepository;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.MAX_LOGIN_ELAPSED_TIME;

@Service
@Transactional
public class LoginMappingServiceImpl implements LoginMappingService {

    @Autowired private LoginMappingRepository mappingRepository;
	@Autowired private CoreConfigurationService configurationService;

	@Override
	public LoginMapping createMapping(String targetURL) {
		LoginMappingEntity mapping = LoginMappingEntity.create(targetURL);
		mappingRepository.persist(mapping);
		return mapping;
	}

	@Override
	public LoginMapping getMapping(String token) {
		if (token == null) return null;
		return mappingRepository.findByToken(token);
	}

	public boolean isExpired(String token) {
        return token == null || isExpired(getMapping(token));
    }

	private boolean isExpired(LoginMapping mapping) {
        return mapping.getCreationTime() + getMaxLoginElapsedTime() < System.currentTimeMillis();
    }

	@Override
	public void purgeExpiredMappings() {
        mappingRepository.findAll().stream()
                         .filter(this::isExpired)
                         .forEach(loginMapping -> mappingRepository.remove(loginMapping));
	}

	@Override
	public void removeMapping(String token) {
		mappingRepository.remove(token);
	}

	private long getMaxLoginElapsedTime() {
		Long maxElapsedTime = configurationService.getValue(MAX_LOGIN_ELAPSED_TIME);
		return Duration.of(maxElapsedTime, MAX_LOGIN_ELAPSED_TIME.getChronoUnit()).toMillis();
	}

}
