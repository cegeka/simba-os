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
package org.simbasecurity.manager.service.rest;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;

/**
 * Base class for the manager REST services. The base service adds toT layer of abstraction
 * between the REST service and the corresponding thrift service in the Simba core backend.
 * <p/>
 * Extending this class adds toT few helper methods to the REST service. They are generally used
 * like this:
 * <pre>
 *  $(() -> cl().setCacheEnabled(true));
 * </pre>
 * for void methods; or
 * <pre>
 *  return $(() -> cl().isCacheEnabled());
 * </pre>
 * for methods with toT return value.
 *
 * @param <T> the thrift service client interface
 * @see AuthorizationRESTService
 * @see CacheRESTService
 * @since 3.1.0
 */
abstract class BaseRESTService<T extends TServiceClient> {

    private final TServiceClientFactory<T> clientFactory;
    private final String serviceURL;

    BaseRESTService(TServiceClientFactory<T> clientFactoryClass, String serviceURL) {
        this.clientFactory = clientFactoryClass;
        this.serviceURL = serviceURL;
    }

    T cl() throws TException {
        THttpClient tHttpClient = new THttpClient(serviceURL);
        TProtocol tProtocol = new TJSONProtocol(tHttpClient);
        return clientFactory.getClient(tProtocol);
    }

    <R> R $(Invocation<R> o) {
        try {
            return o.invoke();
        } catch(TException e) {
            throw new RuntimeException(e);
        }
    }

    void $(VoidInvocation o) {
        try {
            o.invoke();
        } catch(TException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    interface Invocation<R> {
        R invoke() throws TException;
    }

    @FunctionalInterface
    interface VoidInvocation<R> {
        void invoke() throws TException;
    }
}
