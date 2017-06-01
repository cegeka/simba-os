package org.simbasecurity.manager.service.rest;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;

public abstract class BaseRESTService<T extends TServiceClient> {

    private final TServiceClientFactory<T> clientFactory;
    private final String serviceURL;

    BaseRESTService(TServiceClientFactory<T> clientFactoryClass, String serviceURL) {
        this.clientFactory = clientFactoryClass;
        this.serviceURL = serviceURL;
    }

    public T getServiceClient() throws TTransportException {
        THttpClient tHttpClient = new THttpClient(serviceURL);
        TProtocol tProtocol = new TJSONProtocol(tHttpClient);
        return clientFactory.getClient(tProtocol);

    }
}
