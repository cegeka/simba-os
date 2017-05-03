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
package org.simbasecurity.client.filter;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.client.filter.action.FilterActionFactory;
import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.filter.action.MakeCookieAction;
import org.simbasecurity.common.request.RequestUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class SimbaFilter implements Filter {

    private String simbaURL;
    private String simbaWebURL;
    private String simbeEidSuccessUrl;
    private String authenticationChainName;
    private String excludedUrl;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        simbaURL = SystemConfiguration.getSimbaServiceURL(filterConfig);
        if (simbaURL == null) {
            throw new ServletException("Simba URL has not been set. Check org.simbasecurity.client.filter params or system property [" + SystemConfiguration.SYS_PROP_SIMBA_INTERNAL_SERVICE_URL + "]");
        }

        simbaWebURL = SystemConfiguration.getSimbaWebURL(filterConfig);
        if (simbaWebURL == null) {
            throw new ServletException("Simba web URL has not been set. Check org.simbasecurity.client.filter params or system property [" + SystemConfiguration.SYS_PROP_SIMBA_WEB_URL + "]");
        }

        authenticationChainName = SystemConfiguration.getAuthenticationChainName(filterConfig);
        if (authenticationChainName == null) {
            throw new ServletException("Simba authentication chain name has not been set. Check org.simbasecurity.client.filter params or system property ["
                                       + SystemConfiguration.SYS_PROP_SIMBA_AUTHENTICATION_CHAIN_NAME
                                       + "]");
        }

        simbeEidSuccessUrl = SystemConfiguration.getSimbaEidSuccessUrl(filterConfig);
        excludedUrl = SystemConfiguration.getExclusionContextPath(filterConfig);

        MakeCookieAction.setSecureCookiesEnabled(SystemConfiguration.getSecureCookiesEnabled(filterConfig));
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws ServletException, IOException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws ServletException, IOException {
        if (isUrlExcluded(request)) {
            chain.doFilter(request, response);
            return;
        }
        RequestData requestData = RequestUtil.createRequestData(request, simbaWebURL, simbeEidSuccessUrl);

        FilterActionFactory actionFactory = new FilterActionFactory(request, response, chain);

        THttpClient tHttpClient = null;
        try {
            tHttpClient = new THttpClient(SimbaConfiguration.getSimbaAuthenticationURL());
            TProtocol tProtocol = new TJSONProtocol(tHttpClient);

            AuthenticationFilterService.Client authenticationClient = new AuthenticationFilterService.Client(tProtocol);

            ActionDescriptor actionDescriptor = authenticationClient.processRequest(requestData, authenticationChainName);
            actionFactory.execute(actionDescriptor);
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            if (tHttpClient != null) {
                tHttpClient.close();
            }
        }
    }

    @Override
    public void destroy() {
        // no need to release any resource
    }

    boolean isUrlExcluded(HttpServletRequest request) {
        String cp = request.getContextPath();
        String uri = request.getRequestURI();
        String path = uri.substring(cp.length());
        return path.equals(excludedUrl);
    }
}
