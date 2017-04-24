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
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.jaas.callbackhandler.ChainContextCallbackHandler;
import org.simbasecurity.core.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import static org.simbasecurity.core.exception.SimbaMessageKey.LOGIN_FAILED;

/**
 * Manages the JAAS Flow by initializing the LoginContext, calling the login()
 * method and return the Subject when the authentication was successful.
 *
 * @since 1.0
 */
@Component
public class JaasLoginCommand implements Command {

    @Value("${simba.jaas.login.conf.entry}")
    private String loginConfEntry;

    @Autowired private Audit audit;
    @Autowired private CredentialService credentialService;
    @Autowired private AuditLogEventFactory auditLogFactory;

    private String getLoginConfEntry() {
        return loginConfEntry;
    }

    void setLoginConfEntry(String loginConfEntry) {
        this.loginConfEntry = loginConfEntry;
    }

    @Override
    public State execute(ChainContext context) throws Exception {
        String userName = context.getUserName();
        try {
            LoginContext loginContext = new LoginContext(getLoginConfEntry(), new ChainContextCallbackHandler(context));
            loginContext.login();

            credentialService.resetInvalidLoginCount(userName);

            audit.log(auditLogFactory.createEventForAuthenticationForSuccess(context, AuditMessages.JAAS_LOGIN_SUCCESS));

            return State.CONTINUE;
        } catch (LoginException e) {
            audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, AuditMessages.JAAS_LOGIN_FAILED));

            if (credentialService.checkUserStatus(userName, Status.ACTIVE)) {
                boolean blocked = credentialService.increaseInvalidLoginCountAndBlockAccount(userName);
                if (blocked) {
                    audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, AuditMessages.ACCOUNT_BLOCKED));
                }
            }

            context.redirectWithCredentialError(LOGIN_FAILED);
            return State.FINISH;
        }
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

}
