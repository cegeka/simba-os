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
package org.simbasecurity.client.filter.action;

import com.sun.security.auth.UserPrincipal;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.client.filter.request.HttpServletRequestWithPrincipal;
import org.simbasecurity.common.filter.action.AbstractAction;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.Principal;

public final class DoFilterAndSetPrincipalAction extends AbstractAction {

    private DoFilterExtension doFilterExtension;

    public DoFilterAndSetPrincipalAction(final ActionDescriptor actionDescriptor) {
        super(actionDescriptor);
    }

    @Override
    public void execute() throws ServletException, IOException {
        String username = getActionDescriptor().getPrincipal();
        Principal principal = null;
        if (username != null) {
            principal = new UserPrincipal(username);
        }
        if (principal != null) {
            request = new HttpServletRequestWithPrincipal(request, principal);
        }

        if (doFilterExtension != null) doFilterExtension.before();
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (doFilterExtension != null) doFilterExtension.after();
        }
    }

    @Override
    public String toString() {
        return "Continuing filterchain";
    }
}
