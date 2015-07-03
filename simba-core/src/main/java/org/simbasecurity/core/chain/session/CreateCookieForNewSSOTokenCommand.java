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
package org.simbasecurity.core.chain.session;

import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The CreateCookieForNewSSOTokenCommand checks if a new SSO Token has been
 * generated and then makes sure that a SSO cookie is sent back to the user.
 *
 * @since 1.0
 */
@Component
public class CreateCookieForNewSSOTokenCommand implements Command {

    private static final Logger LOG = LoggerFactory.getLogger(CreateCookieForNewSSOTokenCommand.class);

    @Override
    public State execute(ChainContext context) throws Exception {
        if (context.isSsoTokenMappingKeyProvided()) {
            context.activateAction(ActionType.MAKE_COOKIE);
            context.setSSOTokenForActions(context.getCurrentSession().getSSOToken());

            LOG.debug("Schedule make Cookie for new SSO token[{}]", context.getRequestSSOToken());
        }
        return State.CONTINUE;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

}