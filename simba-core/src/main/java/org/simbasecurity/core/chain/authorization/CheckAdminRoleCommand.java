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
package org.simbasecurity.core.chain.authorization;

import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.api.service.thrift.PolicyDecision;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.simbasecurity.core.audit.AuditMessages.ACCESS_DENIED;

@Component
public class CheckAdminRoleCommand implements Command {

	@Autowired
	private AuthorizationService.Iface authorizationService;
	@Autowired
	private Audit audit;
	@Autowired
	private AuditLogEventFactory auditLogFactory;

	@Autowired
	private ConfigurationServiceImpl configurationService;

	@Override
	public State execute(ChainContext context) throws Exception {

		PolicyDecision policyDecision = this.authorizationService.isUserInRole(context.getUserName(),
																			   configurationService.getValue(SimbaConfigurationParameter.ADMIN_ROLE_NAME));
		if (policyDecision.isAllowed()) {

			audit.log(auditLogFactory.createEventForAuthorizationForSuccess(context, AuditMessages.USER_HAS_ADMIN_ROLE));
			return State.CONTINUE;
		}

		audit.log(auditLogFactory.createEventForAuthorizationForFailure(context, ACCESS_DENIED + context.getRequestURL()));
		context.redirectToAccessDenied();

		return State.FINISH;
	}

	@Override
	public boolean postProcess(ChainContext context, Exception exception) {
		return false;
	}

}
