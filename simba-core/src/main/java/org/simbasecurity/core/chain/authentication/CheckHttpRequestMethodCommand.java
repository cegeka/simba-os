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

import static org.simbasecurity.core.audit.AuditMessages.*;

import java.util.ArrayList;
import java.util.List;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckHttpRequestMethodCommand implements Command {

    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;
    
	@Override
	public State execute(ChainContext context) throws Exception {
		List<String> allowedHttpRequestMethods = loadAllowedHttpRequestMethods();
		
		if(!allowedHttpRequestMethods.contains(context.getRequestMethod())){
			logFailure(context, INVALID_HTTP_REQUEST_METHOD);
			context.redirectToAccessDenied();
            return State.FINISH;
		}
		
		logSuccess(context, CHECK_HTTP_REQUEST_METHOD);
        return State.CONTINUE;
	}

	private List<String> loadAllowedHttpRequestMethods() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("POST");
		list.add("GET");
		return list;
	}

	@Override
	public void logFailure(ChainContext context, String message) {
		audit.log(auditLogFactory.createEventForAuthenticationForFailure(context, message));
	}

	@Override
	public void logSuccess(ChainContext context, String message) {
		audit.log(auditLogFactory.createEventForAuthenticationForSuccess(context, message));
	}

	@Override
	public boolean postProcess(ChainContext context, Exception exception) {
		return false;
	}

}
