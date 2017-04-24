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
package org.simbasecurity.servlet;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.api.service.thrift.PolicyDecision;
import org.simbasecurity.common.config.SystemConfiguration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FeedingServlet extends HttpServlet {

	private static final long serialVersionUID = -8841720504649698485L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        THttpClient tHttpClient = null;
        try {
            tHttpClient = new THttpClient(SystemConfiguration.getSimbaServiceURL(getServletContext()) + "/authorizationService");
            TProtocol tProtocol = new TJSONProtocol(tHttpClient);

            AuthorizationService.Client authorizationClient = new AuthorizationService.Client(tProtocol);
            PolicyDecision decision = authorizationClient.isResourceRuleAllowed(request.getUserPrincipal().getName(), "ANIMAL", "WRITE");
            if (!decision.isAllowed()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            response.sendRedirect("jsp/feeding.jsp");
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            if (tHttpClient != null) {
                tHttpClient.close();
            }
        }
	}
}
