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
package org.simbasecurity.core.chain.authentication;

import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.service.ExcludedResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The ExcludeResourceCommand allows authentication exclusion for specific set
 * of URL resources. If the requested resource is in the exclusion list, then
 * the
 * {@link javax.servlet.FilterChain#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}
 * method is executed.
 *
 * @author BKDEVEL
 */
@Component
public class ExcludeResourceCommand implements Command {

    @Autowired private ExcludedResourceService excludeResourceService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        if (excludeResourceService.isResourceExcluded(context.getRequestURL())) {
            if(!excludeResourceService.isResourceLoggingExcluded(context.getRequestURL())) {
        	    logSuccess(context, "Resource excluded [" + context.getRequestURL() + "]");
            }
            context.activateAction(ActionType.DO_FILTER_AND_SET_PRINCIPAL);
            return State.FINISH;
        }
        audit.log(auditLogFactory.createEventForAuthentication(context, AuditMessages.NO_EXCLUDED_RESOURCE + context.getRequestURL()));
        return State.CONTINUE;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }

    @Override
    public void logSuccess(ChainContext context, String message) {
    	audit.log(auditLogFactory.createEventForAuthenticationForSuccess(context, message));
    }

    @Override
    public void logFailure(ChainContext context, String message) {
    	audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, message));
    }

}
