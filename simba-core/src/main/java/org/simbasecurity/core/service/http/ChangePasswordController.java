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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.filter.action.RequestActionFactory;
import org.simbasecurity.common.request.RequestUtil;
import org.simbasecurity.core.chain.Chain;
import org.simbasecurity.core.chain.ChainContextImpl;
import org.simbasecurity.core.service.LoginMappingService;
import org.simbasecurity.core.service.SessionService;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


@org.springframework.stereotype.Controller
@RequestMapping("/simba-change-pwd")
public class ChangePasswordController implements Controller {

    @Autowired
    private Chain credentialChain;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private CoreConfigurationService configurationService;
    
    @Autowired
    private LoginMappingService loginMappingService;

    private SimbaWebUrlResolver simbaWebUrlResolver = new SimbaWebUrlResolver();
    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestData requestData = RequestUtil.createRequestData(request, simbaWebUrlResolver.resolveSimbaWebURL(request));
		ChainContextImpl context = new ChainContextImpl(requestData, sessionService.getSession(requestData.getSsoToken()), configurationService,loginMappingService);
        credentialChain.execute(context);
        
        ActionDescriptor actionDescriptor = context.getActionDescriptor();

        RequestActionFactory actionFactory = new RequestActionFactory(request, response);
        actionFactory.execute(actionDescriptor);

        return null;
    }
}