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
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.common.request.RequestUtil;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JerseyBasicAuthenticationFilter implements ContainerRequestFilter {

    private String simbaWebURL = SystemConfiguration.getSimbaWebURL();

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        ContainerRequest containerRequest = (ContainerRequest) containerRequestContext.getRequest();

        Map<String, String> requestParameters = toMap(containerRequestContext.getUriInfo().getQueryParameters());
        List<String> auth = containerRequest.getRequestHeader("authorization");
        if (auth == null || auth.isEmpty()) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        String[] credentials = decode(auth.get(0));
        requestParameters.put(AuthenticationConstants.USERNAME, credentials[0]);
        requestParameters.put(AuthenticationConstants.PASSWORD, credentials[1]);

        RequestData requestData = new RequestData(requestParameters, toMap(containerRequest.getRequestHeaders()),
                                                  containerRequest.getAbsolutePath().toString(), simbaWebURL, null /* SSO Token */, null /* Client IP */,
                                                  false, false, false, false, false, containerRequest.getMethod(), RequestUtil.HOST_SERVER_NAME, null, null);

        THttpClient tHttpClient = null;
        try {
            tHttpClient = new THttpClient(simbaWebURL + "/authenticationService");
            TProtocol tProtocol = new TJSONProtocol(tHttpClient);

            AuthenticationFilterService.Client authenticationClient = new AuthenticationFilterService.Client(tProtocol);

            ActionDescriptor actionDescriptor = authenticationClient.processRequest(requestData, "wsLoginChain");
            if (!actionDescriptor.getActionTypes().contains(ActionType.DO_FILTER_AND_SET_PRINCIPAL)) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(e, Response.Status.UNAUTHORIZED);
        } finally {
            if (tHttpClient != null) {
                tHttpClient.close();
            }
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

    private String[] decode(String auth) {
        if (auth.toLowerCase().startsWith("basic ")) {
            return decodeBasic(auth);
        }
        throw new UnsupportedOperationException("Only Basic Authentication supported so far");
    }

    private String[] decodeBasic(String auth) {
        String digest = auth.substring(6);

        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(digest);
        if (decodedBytes == null || decodedBytes.length == 0) {
            return null;
        }

        return new String(decodedBytes).split(":", 2);
    }
}
