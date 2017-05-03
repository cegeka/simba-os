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
package org.simbasecurity.core.audit;

import org.simbasecurity.core.task.VerifyAuditLogEvent;

/**
 * Factory to construct a single String message from an AuditLogEvent to use to create an
 * integrity digest for the event.
 */
public class AuditLogIntegrityMessageFactory {

    public static String createMessage(VerifyAuditLogEvent event) {
        return createDigest(event.getTimestamp(), event.getUserName(), event.getSsoToken(), event.getRemoteIp(),
                           event.getMessage(), event.getName(), event.getFirstName(), event.getUserAgent(),
                           event.getHost(), event.getEventCategory(), event.getRequestUrl(), event.getChainId());

    }

    public static String createDigest(AuditLogEvent event, String ssoToken) {
        return createDigest(event.getTimestamp(), event.getUsername(), ssoToken, event.getRemoteIP(),
                           event.getMessage(), event.getName(), event.getFirstName(), event.getUserAgent(),
                           event.getHostServerName(), event.getCategory().name(), event.getRequestURL(), event.getChainId());

    }

    private static String createDigest(long timestamp, String userName, String ssoToken, String remoteIp, String message,
                                      String name, String firstName, String userAgent, String host, String eventCategory, String requestURL, String chainId) {
        return timestamp + userName + ssoToken + remoteIp + message + name + firstName + userAgent
               + host + eventCategory + requestURL + chainId;
    }

}
