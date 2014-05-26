/*
 * Copyright 2011 Simba Open Source
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
package org.simbasecurity.core.audit;

import static org.simbasecurity.core.audit.AuditLogLevel.*;

import org.apache.commons.lang.StringUtils;
import org.simbasecurity.api.service.thrift.SSOToken;

/**
 * @since 1.0
 */
public class AuditLogEvent {

    private static final String NO_TOKEN = "No SSO Token";

    private final AuditLogEventCategory category;
    private final String username;
    private final SSOToken ssoToken;
    private final String remoteIP;
    private final String message;
    private final long timestamp;
    private final String hostServerName;
    private final String name;
    private final String firstName;
    private final String userAgent;
    private final String requestURL;
	private final String chainId;
	
	private AuditLogLevel auditLogLevel;

    public AuditLogEvent(AuditLogEventCategory category, String username, SSOToken ssoToken, String remoteIP,
                         String message, String userAgent, String hostServerName, String surname, String firstName,String requestURL,String chainId) {
        this.timestamp = System.currentTimeMillis();
        this.category = category;
        this.username = truncate(username);
        this.ssoToken = ssoToken;
        this.remoteIP = remoteIP;
        this.message = truncate(message, 512);
        this.name = truncate(surname);
        this.firstName = truncate(firstName);
        this.hostServerName = truncate(hostServerName);
        this.userAgent = truncate(userAgent);
        this.requestURL = truncate(requestURL);
        this.chainId = chainId;
        
        this.auditLogLevel = TRACE;
    }

    private static String truncate(String input) {
        return StringUtils.abbreviate(input, 255);
    }

    private static String truncate(String input, int length) {
        return StringUtils.abbreviate(input, length);
    }

    public String getUsername() {
        return username;
    }

    public SSOToken getSSOToken() {
        return ssoToken;
    }

    public String getSSOTokenString() {
        return ssoToken != null ? ssoToken.getToken() : NO_TOKEN;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHostServerName() {
        return hostServerName;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public AuditLogEventCategory getCategory() {
        return category;
    }

    public String getUserAgent() {
        return userAgent;
    }

	public String getRequestURL() {
		return requestURL;
	}

	public String getChainId() {
		return chainId;
	}
	
	public AuditLogLevel getAuditLogLevel() {
		return auditLogLevel;
	}

	public void markAuditLogToBeArchived() {
		this.auditLogLevel = INFO;
	}
}
