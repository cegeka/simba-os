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

package org.simbasecurity.core.util;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.*;

import org.apache.thrift.TException;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.util.StringUtil;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class URLValidator {

    @Autowired private AuthorizationService.Iface authorizationService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;
    @Autowired private ConfigurationService configurationService;

    public String getValidatedURL(String url, String userName) {
        try {
            if (this.authorizationService.isURLRuleAllowed(userName, url, "POST").isAllowed()) {
                return url;
            }
        } catch (TException ignore) {
        }

        logFailure(url, userName);
        return redirectToAccessDenied();
    }

    private void logFailure(String url, String userName) {
        audit.log(auditLogFactory.createEventForFailureInForm(userName, "Trying to access " + url));
    }

    private String redirectToAccessDenied() {
        String simbaURL = SystemConfiguration.getSimbaWebURL();
        if (StringUtil.isEmpty(simbaURL)) {
            simbaURL = SystemConfiguration.getSimbaServiceURL();
        }
        return simbaURL + configurationService.getValue(ACCESS_DENIED_URL);
    }
}
