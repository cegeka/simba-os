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
package org.simbasecurity.client.authorization.caching;

import org.simbasecurity.common.event.SimbaEvent;
import org.simbasecurity.common.event.SimbaEventType;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import static org.simbasecurity.common.event.SimbaEventType.*;

public class RefreshCacheEventListener implements MessageListener {

    private AuthorizationCachingServiceImpl cachingService;

    public RefreshCacheEventListener(AuthorizationCachingServiceImpl cachingService) {
        this.cachingService = cachingService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            SimbaEvent event = (SimbaEvent) ((ObjectMessage) message).getObject();
            if (event != null) {
                SimbaEventType eventType = event.getEventType();

                if (eventType == AUTHORIZATION_CHANGED) {
                    cachingService.invalidate();
                } else if (eventType == USER_AUTHORIZATION_CHANGED) {
                    cachingService.invalidate(getUserName(event));
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUserName(SimbaEvent event) {
        return (String) event.getValue(USER_AUTHORIZATION_CHANGED.name());
    }
}
