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

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.chain.ChainContext;
import org.springframework.stereotype.Component;

import static org.simbasecurity.core.audit.AuditLogEventCategory.*;
import static org.simbasecurity.core.audit.AuditMessages.FAILURE;
import static org.simbasecurity.core.audit.AuditMessages.SUCCESS;

@Component
public class AuditLogEventFactory {

	public AuditLogEvent createEventForUserPasswordForm(String userName, String message) {
		return new AuditLogEvent(AUTH_LOGIN_FORM, userName, null,null,message,null,null,null,null,null,null);  
    }

	public AuditLogEvent createEventForUserAuthentication(String userName, String message) {
		AuditLogEvent auditLogEvent=new AuditLogEvent(AUTHENTICATION, userName, null,null,message,null,null,null,null,null,null);
		auditLogEvent.markAuditLogToBeArchived();
		return auditLogEvent;
	}
	
	public AuditLogEvent createEventForFailureInForm(String userName, String message) {
		AuditLogEvent auditLogEvent = new AuditLogEvent(AUTH_LOGIN_FORM, userName, null,null,FAILURE + message,null,null,null,null,null,null);
		auditLogEvent.markAuditLogToBeArchived();
		return auditLogEvent;  
    }
			
	public AuditLogEvent createEventForSession(String userName,SSOToken ssoToken,String clientIpAddress, String message) {
        return new AuditLogEvent(SESSION, userName, ssoToken,clientIpAddress,message,null,null,null,null,null,null);
    }
	
	public AuditLogEvent createEventForSession(String userName,SSOToken ssoToken,String clientIpAddress,String hostServerName, String userAgent, String requestURL, String message) {
		return new AuditLogEvent(AuditLogEventCategory.SESSION, userName, ssoToken, clientIpAddress, message, userAgent,requestURL,hostServerName,null,null,null);
    }
	
	public AuditLogEvent createEventForSessionForSuccess(ChainContext context, String message) {
        return createAuditLogEvent(SESSION, context, SUCCESS +  message);
    }
	
	public AuditLogEvent createEventForSessionForFailure(ChainContext context, String message) {
        AuditLogEvent createAuditLogEvent = createAuditLogEvent(SESSION, context, FAILURE +  message);
        createAuditLogEvent.markAuditLogToBeArchived();
		return createAuditLogEvent;
    }
	
	public AuditLogEvent createEventForAuthentication(ChainContext context, String message) {
        return createAuditLogEvent(AUTHENTICATION, context, message);
    }
	
	public AuditLogEvent createEventForAuthenticationForSuccess(ChainContext context, String message) {
        return createAuditLogEvent(AUTHENTICATION, context, SUCCESS +  message);
    }
	
	public AuditLogEvent createEventForAuthenticationForFailure(ChainContext context, String message) {
        AuditLogEvent createAuditLogEvent = createAuditLogEvent(AUTHENTICATION, context, FAILURE +  message);
        createAuditLogEvent.markAuditLogToBeArchived();
		return createAuditLogEvent;
    }
	
	public AuditLogEvent createEventForAuthenticationEID(String userName, String clientIpAddress, String message) {
		return new AuditLogEvent(AuditLogEventCategory.AUTH_EID, userName, null, clientIpAddress, message, null,null,null,null,null,null);
	}
	
	public AuditLogEvent createEventForAuthorizationForSuccess(ChainContext context, String message) {
        return createAuditLogEvent(AUTHOR, context, SUCCESS +  message);
    }
	
	public AuditLogEvent createEventForAuthorizationDecision(String username, String message) {
        return new AuditLogEvent(AUTHOR, username, null,null,message,null,null, null,null,null,null);
    }
	
	public AuditLogEvent createEventForAuthorizationForFailure(ChainContext context, String message) {
        AuditLogEvent createAuditLogEvent = createAuditLogEvent(AUTHOR, context, FAILURE +  message);
        createAuditLogEvent.markAuditLogToBeArchived();
		return createAuditLogEvent;
    }
	
	public AuditLogEvent createEventForChainSuccess(ChainContext context,String message) {
		AuditLogEvent createAuditLogEvent = createAuditLogEvent(CHAIN, context, message);
		createAuditLogEvent.markAuditLogToBeArchived();
		return createAuditLogEvent;
	}
	
	public AuditLogEvent createEventForChainFailure(ChainContext context,String message) {
		AuditLogEvent createAuditLogEvent = createAuditLogEvent(CHAIN, context, message);
		createAuditLogEvent.markAuditLogToBeArchived();
		return createAuditLogEvent;
	}
	
	private AuditLogEvent createAuditLogEvent(AuditLogEventCategory category, ChainContext chainContext, String message) {
		String userName = chainContext.getUserName();
		 if (userName == null) {
	            userName = "not yet logged in";
	        }
		return new AuditLogEvent(category, userName, chainContext.getRequestSSOToken(),
				chainContext.getClientIpAddress(), message, chainContext.getUserAgent(),chainContext.getHostServerName(),null,null,chainContext.getRequestURL(),chainContext.getChainContextId());
	}

	public AuditLogEvent createEventForAuthenticationEIDSAMLResponse(ChainContext chainContext, String messageID, String timestamp, String endUser) {
		String message = "samlMessageID=[" + messageID + "];timestamp=[" + timestamp + "];user=[" + endUser + "]";
		AuditLogEvent auditLogEvent = createAuditLogEvent(AuditLogEventCategory.AUTH_EID_SAML_RESPONSE, chainContext, message);
		auditLogEvent.markAuditLogToBeArchived();
		return auditLogEvent;
	}

	public AuditLogEvent createEventForEIDSAMLResponse(ChainContext context, String message) {
		return createAuditLogEvent(AuditLogEventCategory.AUTH_EID_SAML_RESPONSE, context, message);
	}

	public AuditLogEvent createEventForManagement(String username, SSOToken ssoToken, String message) {
		AuditLogEvent auditLogEvent =
				new AuditLogEvent(MANAGEMENT, username, ssoToken, null, message, null, null, null, null, null, null);
		auditLogEvent.markAuditLogToBeArchived();
		return auditLogEvent;
	}
}

