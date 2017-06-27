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
package org.simbasecurity.core.service.cache;

import org.simbasecurity.api.service.thrift.CacheService;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.event.EventService;
import org.simbasecurity.core.event.SimbaEventType;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("cacheService")
public class CacheServiceImpl implements CacheService.Iface {

    @Autowired private EventService eventService;
    @Autowired private CoreConfigurationService configurationService;

    @Override
    public void refreshCacheIfEnabled() {
        eventService.publish(SimbaEventType.RULE_CHANGED);
        eventService.publish(SimbaEventType.AUTHORIZATION_CHANGED);
    }

    @Override
    public void refreshCacheForUserIfEnabled(String userName) {
        eventService.publish(SimbaEventType.RULE_CHANGED);
        eventService.publish(SimbaEventType.USER_AUTHORIZATION_CHANGED, userName);
    }

    @Override
    public boolean isCacheEnabled() {
        return (Boolean) configurationService.getValue(SimbaConfigurationParameter.CACHING_ENABLED);
    }

    @Override
    @Transactional
    public void setCacheEnabled(boolean isEnabled) {
        configurationService.changeParameter(SimbaConfigurationParameter.CACHING_ENABLED, isEnabled);
    }
}