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

package org.simbasecurity.core.chain.eid;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserEntity;
import org.simbasecurity.core.service.UserService;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Create a new user based on the data received in the SAML Authentication Response.
 * The CreateEIDUserCommand assumes the username and the required metadata to be present
 * on the {@link org.simbasecurity.core.chain.ChainContext}.
 *
 * @since @2.1.3
 */
@Component
public class CreateEIDUserCommand implements Command {

    @Autowired private UserService userService;
    @Autowired private CoreConfigurationService configurationService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        SAMLUser samlUser = context.getSAMLUser();

        User user = userService.findByName(samlUser.getInsz());
        if (user == null) {
            List<String> roles = configurationService.getValue(SimbaConfigurationParameter.DEFAULT_USER_ROLE);

            user = new UserEntity(samlUser.getInsz());
            user.setName(samlUser.getLastname());
            user.setFirstName(samlUser.getFirstname());
            user.setLanguage(getLanguageIfUnknownUseNL(samlUser));
            user.setPasswordChangeRequired(false);
            user.setChangePasswordOnNextLogon(false);

            userService.create(user, roles);
            audit.log(auditLogFactory.createEventForEIDSAMLResponse(context, "New user for eid created with username [" + user.getUserName() + "]"));
        } else {
            user.setName(samlUser.getLastname());
            user.setFirstName(samlUser.getFirstname());
            user.setLanguage(getLanguageIfUnknownUseNL(samlUser));
            audit.log(auditLogFactory.createEventForEIDSAMLResponse(context, "Updated user with username [" + user.getUserName() + "] with new FAS data"));
        }

        context.setUserPrincipal(user.getUserName());

        return State.CONTINUE;
    }

    private Language getLanguageIfUnknownUseNL(SAMLUser samlUser) {
        Language language = Language.fromISO639Code(samlUser.getLanguage());
        return language == null ? Language.nl_NL : language;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }
}
