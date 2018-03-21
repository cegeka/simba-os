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
package org.simbasecurity.core.service.http;

import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.common.filter.action.RequestActionFactory;
import org.simbasecurity.common.request.RequestConstants;
import org.simbasecurity.common.request.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@org.springframework.stereotype.Controller
@RequestMapping("/simba-login")
public class LoginController implements Controller {

    @Autowired private AuthenticationFilterService.Iface authenticationFilterService;

    private SimbaWebUrlResolver simbaWebUrlResolver = new SimbaWebUrlResolver();

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestData requestData = RequestUtil.createRequestData(request, simbaWebUrlResolver.resolveSimbaWebURL(request));

        ActionDescriptor actionDescriptor = authenticationFilterService.processRequest(requestData, "credentialChain");

        if (actionDescriptor.getSsoToken() != null) {
            makeSimbaSSOCookieForCORS(response, actionDescriptor);
        }

        RequestActionFactory actionFactory = new RequestActionFactory(request, response);
        actionFactory.execute(actionDescriptor);

        return null;
    }

    private void makeSimbaSSOCookieForCORS(HttpServletResponse response, ActionDescriptor actionDescriptor) {
        // Path cannot be /simba/manager/ browsers won't accept on redirect if not /
        response.addHeader("Set-Cookie", RequestConstants.SIMBA_SSO_TOKEN + "=" + actionDescriptor.getSsoToken().getToken() + "; HttpOnly; Path=/");
    }



}
