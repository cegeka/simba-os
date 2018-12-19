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

package org.simbasecurity.client.rest.jersey;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.glassfish.jersey.server.ContainerRequest;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.client.principal.SimbaPrincipal;
import org.simbasecurity.client.rest.UserNamePassword;
import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.constants.AuthenticationConstants;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.simbasecurity.api.service.thrift.ActionType.DO_FILTER_AND_SET_PRINCIPAL;
import static org.simbasecurity.common.request.RequestUtil.HOST_SERVER_NAME;

public class JerseyBasicAuthenticationFilter implements ContainerRequestFilter {

    private String simbaWebURL = SystemConfiguration.getSimbaWebURL();

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        ContainerRequest containerRequest = (ContainerRequest) containerRequestContext.getRequest();
        Map<String, String> requestParameters = toMap(containerRequestContext.getUriInfo().getQueryParameters());
        Map<String, String> requestHeaders = toMap(containerRequest.getRequestHeaders());

        List<String> auth = containerRequest.getRequestHeader("authorization");
        if (auth == null || auth.isEmpty()) {
            throw new WebApplicationException(UNAUTHORIZED);
        }
        UserNamePassword userNamePassword = UserNamePassword.fromBasicAuthenticationHeader(auth.get(0));
        requestParameters.put(AuthenticationConstants.USERNAME, userNamePassword.getUserName());
        requestParameters.put(AuthenticationConstants.PASSWORD, userNamePassword.getPassword());

        RequestData requestData = new RequestData(
                requestParameters,
                requestHeaders,
                containerRequest.getAbsolutePath().toString(),
                simbaWebURL,
                null,
                null,
                false,
                false,
                false,
                false,
                false,
                containerRequest.getMethod(),
                HOST_SERVER_NAME,
                null,
                null);

        sendRequest(containerRequest, userNamePassword, requestData);
    }

    private void sendRequest(ContainerRequest containerRequest, UserNamePassword userNamePassword, RequestData requestData) {
        try (THttpClient tHttpClient = new THttpClient(SimbaConfiguration.getSimbaAuthenticationURL())) {
            TProtocol tProtocol = new TJSONProtocol(tHttpClient);

            AuthenticationFilterService.Client authenticationClient = new AuthenticationFilterService.Client(tProtocol);

            ActionDescriptor actionDescriptor = authenticationClient.processRequest(requestData, "wsLoginChain");
            if (!actionDescriptor.getActionTypes().contains(DO_FILTER_AND_SET_PRINCIPAL)) {
                throw new WebApplicationException(UNAUTHORIZED);
            }
            containerRequest.setSecurityContext(new SecurityContextWithPrincipal(containerRequest.getSecurityContext(), new SimbaPrincipal(userNamePassword.getUserName())));
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(e, UNAUTHORIZED);
        }
    }

    private <K, V> Map<K, V> toMap(Map<K, List<V>> map) {
        Map<K, V> result = new HashMap<>();
        for (Map.Entry<K, List<V>> entry : map.entrySet()) {
            List<V> list = entry.getValue();
            if (list.size() > 0) {
                result.put(entry.getKey(), list.get(0));
            }
        }
        return result;
    }

}
