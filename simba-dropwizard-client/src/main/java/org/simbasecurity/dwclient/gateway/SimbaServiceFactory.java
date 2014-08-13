package org.simbasecurity.dwclient.gateway;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService.Client;

public class SimbaServiceFactory {

	public THttpClient createTHttpClient(String serviceNameURL) throws TTransportException {
		return new THttpClient(serviceNameURL);
	}

	public Client createJSONAuthenticationFilterService(THttpClient tHttpClient) {
		TProtocol tProtocol = new TJSONProtocol(tHttpClient);
		return new AuthenticationFilterService.Client(tProtocol);
	}

}
