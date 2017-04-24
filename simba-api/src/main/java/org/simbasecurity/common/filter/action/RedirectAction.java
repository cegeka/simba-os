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
package org.simbasecurity.common.filter.action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.simbasecurity.api.service.thrift.ActionDescriptor;

public class RedirectAction extends AbstractAction {

    RedirectAction(ActionDescriptor actionDescriptor) {
        super(actionDescriptor);
    }

    public void execute() throws ServletException, IOException {
        String targetURL = getActionDescriptor().getRedirectURL();
        assertNotNull(targetURL, "TargetURL must be set!");
        redirect(response, targetURL);
    }

    private void redirect(HttpServletResponse response, String targetURL) throws IOException {
        response.sendRedirect(targetURL);
    }

    @Override
    public String toString() {
        return "Redirecting to " + getActionDescriptor().getRedirectURL();
    }

}
