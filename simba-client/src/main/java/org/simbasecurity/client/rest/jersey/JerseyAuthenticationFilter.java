package org.simbasecurity.client.rest.jersey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.common.request.RequestUtil;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class JerseyAuthenticationFilter implements ContainerRequestFilter {

    private String simbaWebURL = SystemConfiguration.getSimbaWebURL();

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
    	
        Map<String, String> requestParameters = toMap(containerRequest.getQueryParameters());
        String auth = containerRequest.getHeaderValue("authorization");
        if (auth == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        String[] credentials = decode(auth);
        requestParameters.put(AuthenticationConstants.USERNAME, credentials[0]);
        requestParameters.put(AuthenticationConstants.PASSWORD, credentials[1]);

        RequestData requestData = new RequestData(requestParameters, toMap(containerRequest.getRequestHeaders()),
                                                  containerRequest.getAbsolutePath().toString(), simbaWebURL, null /* SSO Token */, null /* Client IP */,
                                                  false, false, false, false, false, containerRequest.getMethod(), RequestUtil.HOST_SERVER_NAME, null);

        THttpClient tHttpClient = null;
        try {
            tHttpClient = new THttpClient(simbaWebURL + "authenticationService");
            TProtocol tProtocol = new TJSONProtocol(tHttpClient);

            AuthenticationFilterService.Client authenticationClient = new AuthenticationFilterService.Client(tProtocol);

            ActionDescriptor actionDescriptor = authenticationClient.processRequest(requestData, "wsLoginChain");
            if (!actionDescriptor.getActionTypes().contains(ActionType.DO_FILTER_AND_SET_PRINCIPAL)) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }

            return containerRequest;
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
        Map<K, V> result = new HashMap<K, V>();
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
