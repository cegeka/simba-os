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
package org.simbasecurity.core.chain.authentication;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.LoginMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.simbasecurity.common.constants.AuthenticationConstants.PASSWORD;
import static org.simbasecurity.core.audit.AuditMessages.EMPTY_USERNAME;

/**
 * The ValidateRequestParametersCommand checks if the request contains a user
 * name and password entry. If one of those is not present, the user gets a
 * message about this. The Command also contains a Cross side scripting check by
 * using the UserNameValidator and PasswordValidator to check if they are valid.
 * 
 * @since 1.0
 */
@Component
public final class ValidateRequestParametersCommand implements Command {

    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;
    @Autowired private LoginMappingService loginMappingService;

	@Override
	public State execute(ChainContext context) throws Exception {
		if (!checkEmptyContextParameters(context)) {
			return State.FINISH;
		}

		audit.log(auditLogFactory.createEventForAuthenticationForSuccess(context, AuditMessages.VALID_REQUEST_PARAM));
		return State.CONTINUE;
	}

	/**
	 * Check if the context parameters (username, password) are empty.
	 * 
	 * @param context
	 *            ChainContext
	 * @return boolean true: parameters contain data; false: parameters are
	 *         empty
	 */
	private boolean checkEmptyContextParameters(ChainContext context) {
		if (isEmpty(context.getUserName())) {
			audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, EMPTY_USERNAME));
			context.redirectWithCredentialError(SimbaMessageKey.EMPTY_USERNAME);
			return false;
		}
		if (isEmpty(getPassword(context))) {
			audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, AuditMessages.EMPTY_PASSWORD));
			context.redirectWithCredentialError(SimbaMessageKey.EMPTY_PASSWORD);
			return false;
		}

        try {
            checkIfLoginTokenExpired(context);
        } catch (SimbaException e) {
			audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, e.getMessageKey().name()));
			context.redirectWhenLoginTokenExpired();
            return false;
        }

        return true;
	}

	private String getPassword(ChainContext context) {
		return context.getRequestParameter(PASSWORD);
	}

    /**
     * Validates the login token to see if it exists in the logging mapping
     * database. If the login token isn't present no error is generated because
     * there are cases that there is no login token or it can be null.
     *
     * @param context
     */
    private void checkIfLoginTokenExpired(ChainContext context) {
        String loginToken = context.getLoginToken();
        if(loginToken != null){
            LoginMapping loginMapping = loginMappingService.getMapping(loginToken);
            if(loginMapping == null){
                throw new SimbaException(SimbaMessageKey.LOGIN_TIME_EXPIRED);
            }
            context.setLoginMapping(loginMapping);
        }
    }

	public boolean postProcess(ChainContext context, Exception exception) {
		return false;
	}

}
